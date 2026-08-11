package com.srap.nga.ui.component.fav

import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavCheckBox(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    description: String? = null,
) {
    val segmentedShapes = ListItemDefaults.segmentedShapes(index = index, count = count)
    SegmentedListItem(
        checked = checked,
        onCheckedChange = onCheckedChange,
        shapes = segmentedShapes.copy(
            selectedShape = segmentedShapes.shape,
            pressedShape = segmentedShapes.shape,
            focusedShape = segmentedShapes.shape,
            hoveredShape = segmentedShapes.shape,
            draggedShape = segmentedShapes.shape,
        ),
        modifier = modifier,
        supportingContent = if (description == null) {
            null
        } else {
            { Text(description) }
        },
        trailingContent = {
            Checkbox(
                checked = checked,
                onCheckedChange = null,
            )
        },
    ) {
        Text(title)
    }
}
