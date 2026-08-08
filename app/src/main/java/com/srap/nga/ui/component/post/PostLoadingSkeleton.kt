package com.srap.nga.ui.component.post

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp

@Composable
fun PostLoadingSkeleton(
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "post-skeleton")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "post-skeleton-pulse",
    )
    val skeletonColor = lerp(
        MaterialTheme.colorScheme.surfaceContainerHigh,
        MaterialTheme.colorScheme.surfaceContainerHighest,
        pulse,
    )

    Column(modifier = modifier.fillMaxWidth()) {
        PostBodySkeleton(color = skeletonColor)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        CommentHeaderSkeleton(color = skeletonColor)
        repeat(3) {
            ReplySkeleton(color = skeletonColor)
            HorizontalDivider(
                modifier = Modifier.padding(start = 68.dp, end = 12.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
            )
        }
    }
}

@Composable
fun PostLoadError(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "帖子加载失败",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(16.dp))
        FilledTonalButton(onClick = onRetry) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("重试")
        }
    }
}

@Composable
private fun PostBodySkeleton(color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
    ) {
        AuthorSkeleton(color = color)
        Spacer(modifier = Modifier.height(14.dp))
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp),
            color = color,
        )
        Spacer(modifier = Modifier.height(9.dp))
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(14.dp),
            color = color,
        )
        Spacer(modifier = Modifier.height(9.dp))
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.68f)
                .height(14.dp),
            color = color,
        )
        Spacer(modifier = Modifier.height(9.dp))
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .height(14.dp),
            color = color,
        )
        Spacer(modifier = Modifier.height(9.dp))
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .height(14.dp),
            color = color,
        )
        Spacer(modifier = Modifier.height(14.dp))
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            color = color,
            rounded = true,
        )
        Spacer(modifier = Modifier.height(14.dp))
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp),
            color = color,
        )
        Spacer(modifier = Modifier.height(9.dp))
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(14.dp),
            color = color,
        )
        Spacer(modifier = Modifier.height(9.dp))
        SkeletonBlock(
            modifier = Modifier
                .fillMaxWidth(0.62f)
                .height(14.dp),
            color = color,
        )
    }
}

@Composable
private fun CommentHeaderSkeleton(color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkeletonBlock(
            modifier = Modifier.size(20.dp),
            color = color,
            circle = true,
        )
        Spacer(modifier = Modifier.width(8.dp))
        SkeletonBlock(
            modifier = Modifier
                .width(52.dp)
                .height(16.dp),
            color = color,
        )
        Spacer(modifier = Modifier.weight(1f))
        SkeletonBlock(
            modifier = Modifier
                .width(36.dp)
                .height(12.dp),
            color = color,
        )
    }
}

@Composable
private fun ReplySkeleton(color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        SkeletonBlock(
            modifier = Modifier.size(40.dp),
            color = color,
            circle = true,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.34f)
                    .height(13.dp),
                color = color,
            )
            Spacer(modifier = Modifier.height(10.dp))
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = color,
            )
            Spacer(modifier = Modifier.height(8.dp))
            SkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth(0.76f)
                    .height(12.dp),
                color = color,
            )
        }
    }
}

@Composable
private fun AuthorSkeleton(color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SkeletonBlock(
            modifier = Modifier.size(40.dp),
            color = color,
            circle = true,
        )
        Spacer(modifier = Modifier.width(16.dp))
        SkeletonBlock(
            modifier = Modifier
                .width(104.dp)
                .height(14.dp),
            color = color,
        )
    }
}

@Composable
private fun SkeletonBlock(
    modifier: Modifier,
    color: Color,
    circle: Boolean = false,
    rounded: Boolean = false,
) {
    val shape = when {
        circle -> CircleShape
        rounded -> MaterialTheme.shapes.medium
        else -> MaterialTheme.shapes.extraSmall
    }
    Spacer(
        modifier = modifier
            .clip(shape)
            .background(color),
    )
}
