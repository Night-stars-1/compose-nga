package com.srap.nga.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PersonOutline
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
import androidx.compose.ui.graphics.vector.ImageVector
import com.srap.nga.ui.home.HomeScreen
import com.srap.nga.ui.navigateToFavorite
import com.srap.nga.ui.topic.category.TopicCateGoryScreen
import com.srap.nga.ui.userinfo.UserInfoScreen
import com.srap.nga.utils.StorageUtil

data class NavigationItem(
    val name: String,
    val icon: ImageVector,
    val content: @Composable () -> Unit,
)

@Composable
fun MainScreen(
    onViewPost: (Int) -> Unit,
    onViewTopicSubject: (Int, Boolean?) -> Unit,
    onSearch: () -> Unit,
    onViewLogin: () -> Unit,
    onViewFavorite: () -> Unit = {},
) {
    val navigationData = listOf(
        NavigationItem(
            name = "主页",
            icon = Icons.Filled.Home,
            content = { HomeScreen(onViewPost, onSearch) }
        ),
        NavigationItem(
            name = "社区",
            icon = Icons.Filled.Topic,
            content = { TopicCateGoryScreen(onViewTopicSubject, onSearch) }
        ),
        NavigationItem(
            name = "我的",
            icon = Icons.Filled.PersonOutline,
            content = { UserInfoScreen(
                id = StorageUtil.Uid,
                onViewPost = onViewPost,
                onViewLogin = onViewLogin,
                onBackClick = null,
                onViewFavorite = onViewFavorite,
            ) }
        )
    )

    var selectIndex by rememberSaveable { mutableIntStateOf(0) }
    var previousIndex by rememberSaveable { mutableIntStateOf(0) }
    val savableStateHolder = rememberSaveableStateHolder()
    fun setSelectIndex(index: Int) {
        previousIndex = selectIndex
        selectIndex = index
    }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            navigationData.forEachIndexed { index, item ->
                item(
                    selected = selectIndex == index,
                    label = {
                        if (selectIndex == index) Text(item.name)
                    },
                    onClick = {
                        setSelectIndex(index)
                    },
                    icon = {
                        Icon(item.icon, contentDescription = item.name)
                    }
                )
            }
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
                    navigationData[index].content()
                }
            )
        }
    }
}