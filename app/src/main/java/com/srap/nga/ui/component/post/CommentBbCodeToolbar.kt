package com.srap.nga.ui.component.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.StrikethroughS
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.srap.nga.utils.EmojiUtils
import kotlinx.coroutines.launch

/**
 * 在当前选区外包裹一对 BBCode 标签。
 * 有选中文字时包裹选区并把光标移到闭合标签之后，否则光标停在标签中间方便直接输入。
 */
fun TextFieldValue.insertBbCodeTag(prefix: String, suffix: String): TextFieldValue {
    val start = selection.min
    val end = selection.max
    val selected = text.substring(start, end)
    val newText = buildString {
        append(text, 0, start)
        append(prefix)
        append(selected)
        append(suffix)
        append(text, end, text.length)
    }
    val cursor = if (selected.isEmpty()) {
        start + prefix.length
    } else {
        start + prefix.length + selected.length + suffix.length
    }
    return TextFieldValue(text = newText, selection = TextRange(cursor))
}

/** 在当前选区处插入一段文本（如表情代码），光标移到插入内容之后。 */
fun TextFieldValue.insertSnippet(snippet: String): TextFieldValue {
    val start = selection.min
    val end = selection.max
    val newText = text.substring(0, start) + snippet + text.substring(end)
    return TextFieldValue(text = newText, selection = TextRange(start + snippet.length))
}

private class BbCodeAction(
    val icon: ImageVector,
    val label: String,
    val prefix: String,
    val suffix: String,
)

private val bbCodeActions = listOf(
    BbCodeAction(Icons.Default.FormatBold, "粗体", "[b]", "[/b]"),
    BbCodeAction(Icons.Default.FormatItalic, "斜体", "[i]", "[/i]"),
    BbCodeAction(Icons.Default.FormatUnderlined, "下划线", "[u]", "[/u]"),
    BbCodeAction(Icons.Default.StrikethroughS, "删除线", "[del]", "[/del]"),
    BbCodeAction(Icons.Default.FormatQuote, "引用", "[quote]", "[/quote]"),
    BbCodeAction(Icons.Default.Link, "链接", "[url]", "[/url]"),
    BbCodeAction(Icons.Default.Image, "图片", "[img]", "[/img]"),
    BbCodeAction(Icons.Default.UnfoldLess, "折叠", "[collapse]", "[/collapse]"),
)

/** NGA 发帖支持的字号百分比。 */
private val fontSizePercents = listOf(60, 80, 120, 140, 160)

/** NGA 发帖调色板支持的颜色名及其预览色。 */
private val ngaColors = listOf(
    "red" to Color(0xFFFF0000),
    "crimson" to Color(0xFFDC143C),
    "firebrick" to Color(0xFFB22222),
    "darkred" to Color(0xFF8B0000),
    "orangered" to Color(0xFFFF4500),
    "tomato" to Color(0xFFFF6347),
    "coral" to Color(0xFFFF7F50),
    "orange" to Color(0xFFFFA500),
    "sandybrown" to Color(0xFFF4A460),
    "burlywood" to Color(0xFFDEB887),
    "chocolate" to Color(0xFFD2691E),
    "sienna" to Color(0xFFA0522D),
    "limegreen" to Color(0xFF32CD32),
    "green" to Color(0xFF008000),
    "seagreen" to Color(0xFF2E8B57),
    "teal" to Color(0xFF008080),
    "skyblue" to Color(0xFF87CEEB),
    "royalblue" to Color(0xFF4169E1),
    "blue" to Color(0xFF0000FF),
    "darkblue" to Color(0xFF00008B),
    "indigo" to Color(0xFF4B0082),
    "purple" to Color(0xFF800080),
    "deeppink" to Color(0xFFFF1493),
    "silver" to Color(0xFFC0C0C0),
)

/** 评论输入栏上方的 BBCode 格式工具栏。 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CommentBbCodeToolbar(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    emojiPanelVisible: Boolean,
    onToggleEmojiPanel: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconToggleButton(
            checked = emojiPanelVisible,
            onCheckedChange = { onToggleEmojiPanel() },
            enabled = enabled,
            shapes = IconButtonDefaults.toggleableShapes(),
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEmotions,
                contentDescription = "表情",
            )
        }
        bbCodeActions.forEach { action ->
            IconButton(
                onClick = {
                    onValueChange(value.insertBbCodeTag(action.prefix, action.suffix))
                },
                enabled = enabled,
                shapes = IconButtonDefaults.shapes(),
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.label,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        FontSizeMenuButton(enabled = enabled) { percent ->
            onValueChange(value.insertBbCodeTag("[size=$percent%]", "[/size]"))
        }
        ColorMenuButton(enabled = enabled) { colorName ->
            onValueChange(value.insertBbCodeTag("[color=$colorName]", "[/color]"))
        }
    }
}

@Composable
private fun FontSizeMenuButton(
    enabled: Boolean,
    onSizeSelected: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = { expanded = true },
            enabled = enabled,
            shapes = IconButtonDefaults.shapes(),
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                imageVector = Icons.Default.FormatSize,
                contentDescription = "字号",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            fontSizePercents.forEach { percent ->
                DropdownMenuItem(
                    text = { Text("$percent%") },
                    onClick = {
                        expanded = false
                        onSizeSelected(percent)
                    },
                )
            }
        }
    }
}

@Composable
private fun ColorMenuButton(
    enabled: Boolean,
    onColorSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(
            onClick = { expanded = true },
            enabled = enabled,
            shapes = IconButtonDefaults.shapes(),
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = "文字颜色",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            ngaColors.chunked(6).forEach { rowColors ->
                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)) {
                    rowColors.forEach { (name, color) ->
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable {
                                    expanded = false
                                    onColorSelected(name)
                                }
                                .semantics { contentDescription = name },
                        )
                    }
                }
            }
        }
    }
}

/** 表情分组标签，键为表情代码中的分组前缀。 */
private val emojiCategoryLabels = mapOf(
    "0" to "默认",
    "ac" to "AC娘",
    "a2" to "AC娘2",
    "ng" to "NGA",
    "pst" to "Pst",
    "dt" to "Dt",
    "pg" to "企鹅",
)

/** NGA 表情选择面板，左右滑动或点击分组标签切换分组，点击表情后通过 [onEmojiSelected] 返回表情代码（如 `[s:ac:茶]`）。 */
@Composable
fun EmojiPickerPanel(
    onEmojiSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val categories = remember {
        EmojiUtils.emojiMap.entries.groupBy(
            keySelector = { it.key.removePrefix("[s:").substringBefore(":") },
            valueTransform = { it.key to it.value },
        )
    }
    val categoryKeys = remember(categories) { categories.keys.toList() }
    val pagerState = rememberPagerState(pageCount = { categoryKeys.size })
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp),
        ) {
            categoryKeys.forEachIndexed { index, category ->
                FilterChip(
                    selected = index == pagerState.currentPage,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    label = { Text(emojiCategoryLabels[category] ?: category) },
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
        ) { page ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 44.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(
                    items = categories[categoryKeys[page]].orEmpty(),
                    key = { it.first },
                ) { (code, resId) ->
                    // 默认分组的表情是 GIF，painterResource 不支持，统一走 Coil 加载。
                    AsyncImage(
                        model = resId,
                        contentDescription = code,
                        modifier = Modifier
                            .clickable { onEmojiSelected(code) }
                            .padding(6.dp)
                            .size(32.dp),
                    )
                }
            }
        }
    }
}
