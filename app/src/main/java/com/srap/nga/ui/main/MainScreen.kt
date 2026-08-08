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
import com.srap.nga.ui.theme.AppMotion
import com.srap.nga.ui.topic.category.TopicCateGoryScreen
import com.srap.nga.ui.userinfo.UserInfoScreen

data class NavigationItem(
    val name: String,
    val icon: ImageVector,
)

@Composable
fun MainScreen(
    userId: Int,
    isLoggedIn: Boolean,
    onViewPost: (Int) -> Unit,
    onViewTopicSubject: (Int, Boolean?) -> Unit,
    onSearch: () -> Unit,
    onViewLogin: () -> Unit,
    onViewFavorite: () -> Unit = {},
    openUrl: (String) -> Unit,
) {
    val navigationData = listOf(
        NavigationItem(
            name = "主页",
            icon = Icons.Filled.Home,
        ),
        NavigationItem(
            name = "社区",
            icon = Icons.Filled.Topic,
        ),
        NavigationItem(
            name = "我的",
            icon = Icons.Filled.PersonOutline,
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
                        Text(item.name)
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
                if (targetState >= previousIndex) {
                    slideInHorizontally(
                        animationSpec = AppMotion.defaultSpatial(),
                    ) { it } togetherWith slideOutHorizontally(
                        animationSpec = AppMotion.fastSpatial(),
                    ) { -it }
                } else {
                    slideInHorizontally(
                        animationSpec = AppMotion.defaultSpatial(),
                    ) { -it } togetherWith slideOutHorizontally(
                        animationSpec = AppMotion.fastSpatial(),
                    ) { it }
                }
            },
        ) { index ->
            savableStateHolder.SaveableStateProvider(
                key = index,
                content = {
                    when (index) {
                        0 -> HomeScreen(
                            onViewPost = onViewPost,
                            onSearch = onSearch,
                            openUrl = openUrl,
                        )

                        1 -> TopicCateGoryScreen(onViewTopicSubject, onSearch)
                        else -> UserInfoScreen(
                            id = userId,
                            isLoggedIn = isLoggedIn,
                            onViewPost = onViewPost,
                            onViewLogin = onViewLogin,
                            onBackClick = null,
                            onViewFavorite = onViewFavorite,
                        )
                    }
                }
            )
        }
    }
}
