package com.srap.nga.ui.component.modal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModalBottomActionSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    sheetGesturesEnabled: Boolean = true,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = 0.dp,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.modalWindowInsets },
    properties: ModalBottomSheetProperties =
        ModalBottomSheetProperties(
            isAppearanceLightStatusBars = contentColor.isDark(),
            isAppearanceLightNavigationBars = contentColor.isDark()
        ),
    contentModifier: Modifier = Modifier,
    fillContent: Boolean = false,
    keepActionsVisible: Boolean = false,
    actions: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        sheetMaxWidth = sheetMaxWidth,
        sheetGesturesEnabled = sheetGesturesEnabled,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        scrimColor = scrimColor,
        dragHandle = dragHandle,
        contentWindowInsets = contentWindowInsets,
        properties = properties,
    ) {
        if (keepActionsVisible) {
            // Keep the sheet surface full-height for both anchors while sizing its visible
            // content to the current window area so the footer stays above the navigation bar.
            val density = LocalDensity.current
            val bottomInset = WindowInsets.safeDrawing.getBottom(density)
            var visibleHeightPx by remember { mutableIntStateOf(0) }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .onGloballyPositioned { coordinates ->
                        val root = coordinates.findRootCoordinates()
                        val top = coordinates.positionInRoot().y.roundToInt()
                        val height = (root.size.height - bottomInset - top).coerceAtLeast(0)
                        if (visibleHeightPx != height) {
                            visibleHeightPx = height
                        }
                    }
            ) {
                ActionSheetContent(
                    modifier = contentModifier
                        .fillMaxWidth()
                        .height(with(density) { visibleHeightPx.toDp() }),
                    fillContent = fillContent,
                    actions = actions,
                    content = content,
                )
            }
        } else {
            ActionSheetContent(
                modifier = contentModifier.fillMaxWidth(),
                fillContent = fillContent,
                actions = actions,
                content = content,
            )
        }
    }
}

@Composable
private fun ActionSheetContent(
    modifier: Modifier,
    fillContent: Boolean,
    actions: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .weight(1f, fill = fillContent)
        ) {
            content()
        }

        ActionRow(actions = actions)
    }
}

@Composable
private fun ActionRow(
    actions: @Composable RowScope.() -> Unit,
) {
    HorizontalDivider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
    ) {
        actions()
    }
}

internal fun Color.isDark(): Boolean {
    return this != Color.Transparent && luminance() <= 0.5
}
