package com.srap.nga.ui.component.state

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.runtime.State
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs

private const val VisibleThreshold = 100
/**
 * @param SwipeableState
 * first - 滑动的最小范围，用于用户松手后的复位
 *
 * second - 滑动的判断阈值，当向上滑动高度大于该值时，将会触发复位
 *
 * **注意:** 该值只会在用户上滑的时候生效
 *
 * third - 滑动的最大范围，用于用户松手后的复位
 *
 * @param heightRange 可滑动的高度范围
 */
@Composable
fun rememberSwipeableState(
    anchors: Triple<Float, Float, Float>,
    heightRange: Pair<Float, Float> = Pair(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY)
): SwipeableState {
    return rememberSaveable(
        saver = SwipeableState.Saver(anchors, heightRange)
    ) {
        SwipeableState(anchors, heightRange)
    }
}

@Stable
open class SwipeableState(
    internal val anchors: Triple<Float, Float, Float>,
    internal val heightRange: Pair<Float, Float>,
    internal val animationSpec: AnimationSpec<Float> = SpringSpec(),
) {
    /**
     * 当前的滑动偏移量，< 0 为屏幕上方
     *
     * 该偏移量为限制后的偏移量
     */
    var currentValue by mutableFloatStateOf(0f)
        private set

    /**
     * 是否在播放滑动过程的动画
     */
    var isAnimationRunning by mutableStateOf(false)
        private set

    /**
     * 提示框是否不可见
     */
    val isVisible by derivedStateOf {
        offset.value > VisibleThreshold
    }

    /**
     * 滑动偏移量，< 0 为屏幕上方
     */
    val offset: State<Float> get() = offsetState
    /**
     * 滑动偏移量，< 0 为屏幕上方
     */
    private val offsetState = mutableFloatStateOf(anchors.third)

    /**
     * 滑动的状态
     *
     * 用于更新滑动偏移量
     */
    internal val draggableState = DraggableState {
        val newAbsolute = offsetState.floatValue + it
        val clamped = newAbsolute.coerceIn(heightRange.first, heightRange.second)
        offsetState.floatValue = clamped
    }

    private suspend fun animateInternalToOffset(target: Float, spec: AnimationSpec<Float>) {
        draggableState.drag {
            var prevValue = offsetState.floatValue
            isAnimationRunning = true
            try {
                Animatable(prevValue).animateTo(target, spec) {
                    dragBy(this.value - prevValue)
                    prevValue = this.value
                }
            } finally {
                isAnimationRunning = false
            }
        }
    }

    suspend fun animateTo(targetOffset: Float, anim: AnimationSpec<Float> = animationSpec) {
        try {
            animateInternalToOffset(targetOffset, anim)
        } finally {
            currentValue = offsetState.floatValue
        }
    }

    suspend fun close() {
        animateTo(anchors.third)
    }

    /**
     * 计算滑动复位后的位置
     *
     * @param offset 优化当前滑动的偏移量
     */
    private fun computeTarget(
        offset: Float
    ): Float {
        val result = if (currentValue > 0) {
            if (offset < anchors.second) {
                anchors.first
            } else {
                anchors.third
            }
        } else {
            if (offset < anchors.first) {
                anchors.first
            } else {
                anchors.third
            }
        }

        return result
    }

    suspend fun performFling() {
        val targetValue = computeTarget(
            offset = offset.value
        )
        animateTo(targetValue)
    }

    fun performDrag(delta: Float): Float {
        val potentiallyConsumed = offsetState.floatValue + delta
        val clamped = potentiallyConsumed.coerceIn(heightRange.first, heightRange.second)
        val deltaToConsume = clamped - offsetState.floatValue
        if (abs(deltaToConsume) > 0) {
            draggableState.dispatchRawDelta(deltaToConsume)
        }
        return deltaToConsume
    }


    companion object {
        fun Saver(
            anchors: Triple<Float, Float, Float>,
            heightRange: Pair<Float, Float>,
            animationSpec: AnimationSpec<Float> = SpringSpec()
        ): Saver<SwipeableState, List<Float>> = Saver(
            save = { state ->
                listOf(state.offsetState.floatValue)
            },
            restore = { restored ->
                val current = restored[0]
                SwipeableState(anchors, heightRange, animationSpec).apply {
                    offsetState.floatValue = current
                }
            }
        )
    }
}

internal val SwipeableState.nestedScrollConnection: NestedScrollConnection
    get() = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.toFloat()
            /**
             * offset.value < 100
             * 只在底部提示框出现的时候抵消父组件的滚动
             * 100 是为了让提示框完全滚动下去
             */
            return if (delta > 0 && offset.value < VisibleThreshold && source == NestedScrollSource.UserInput) {
                // 该次滚动消费的available
                // 用来在底部提示框出现的时候抵消父组件的滚动
                performDrag(delta).toOffset()
            } else Offset.Zero // 向下不消费available
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            return if (source == NestedScrollSource.UserInput) {
                performDrag(available.toFloat()).toOffset()
            } else Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            Log.i("TAG", "onPostFling")
            performFling()
            return available
        }

        private fun Float.toOffset(): Offset = Offset(0f, this)

        private fun Offset.toFloat(): Float = this.y
    }
