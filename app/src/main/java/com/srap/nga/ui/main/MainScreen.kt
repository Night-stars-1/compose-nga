package com.srap.nga.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Topic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.srap.nga.ui.home.HomeScreen
import com.srap.nga.ui.topic.category.TopicCateGoryScreen

@Composable
fun MainScreen(
    onViewPost: (Int) -> Unit,
    onViewTopicSubject: (Int) -> Unit,
) {
    var selectIndex by rememberSaveable { mutableIntStateOf(0) }
    var previousIndex by rememberSaveable { mutableIntStateOf(0) }
    val savableStateHolder = rememberSaveableStateHolder()
    fun setSelectIndex(index: Int) {
        previousIndex = selectIndex
        selectIndex = index
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            item(
                selected = selectIndex == 0,
                label = {
                    if (selectIndex == 0) Text("主页")
                },
                onClick = {
                    setSelectIndex(0)
                },
                icon = {
                    Icon(Icons.Filled.Home, contentDescription = "主页")
                }
            )

            item(
                selected = selectIndex == 1,
                label = {
                    if (selectIndex == 1) Text("社区")
                },
                onClick = {
                    setSelectIndex(1)
                },
                icon = {
                    Icon(Icons.Filled.Topic, contentDescription = "社区")
                }
            )
        }
    ) {
        AnimatedContent(
            targetState = selectIndex,
            label = "MainScreen",
            transitionSpec = {
                if (selectIndex >= previousIndex) {
                    // 前进动画
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                } else {
                    // 后退动画
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                }
            },
        ) { index ->
            savableStateHolder.SaveableStateProvider(
                key = index,
                content = {
                    when (index) {
                        0 -> HomeScreen(onViewPost)
                        1 -> TopicCateGoryScreen(onViewTopicSubject)
                    }
                }
            )
        }
    }
}