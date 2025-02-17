package com.srap.nga.ui.component.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun ExtendedNestedScroll(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    var rootOffset by rememberSaveable { mutableFloatStateOf(0f) }
    var headerHeight by remember { mutableStateOf(0.dp) }
    var nestedScrollOffset by remember { mutableFloatStateOf(0f) }
    var targetPercent by remember { mutableFloatStateOf(1f) }
    val current = LocalDensity.current
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput) {
                    return Offset.Zero
                }

                val potentiallyConsumed = nestedScrollOffset + available.y
                val clamped = potentiallyConsumed.coerceIn(-rootOffset, 0f)
                val deltaToConsume = potentiallyConsumed - clamped
                nestedScrollOffset = clamped
                targetPercent = 1 - abs(abs(clamped) / rootOffset)
                return Offset(0f, available.y-deltaToConsume)
            }
        }
    }

    Column(
        modifier = modifier
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            modifier = Modifier
                .height(
                    if (headerHeight != 0.dp) headerHeight * targetPercent
                    else Dp.Unspecified
                )
                .onGloballyPositioned { coordinates ->
                    if (rootOffset == 0f) {
                        rootOffset = coordinates.positionInRoot().y + 20
                        headerHeight = with(current) { coordinates.size.height.toDp() }
                    }
                }
        ) {
            header()
        }

        content()
    }
}