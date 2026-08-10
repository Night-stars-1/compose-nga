package com.srap.nga.ui

import android.net.Uri
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.srap.nga.ui.fav.FavoriteContentScreen
import com.srap.nga.ui.fav.FavoriteScreen
import com.srap.nga.ui.image.ImagePreviewScreen
import com.srap.nga.ui.login.qrcode.QRCodeLoginScreen
import com.srap.nga.ui.main.MainScreen
import com.srap.nga.ui.post.PostScreen
import com.srap.nga.ui.search.SearchScreen
import com.srap.nga.ui.search.result.SearchResultScreen
import com.srap.nga.ui.settings.SettingsScreen
import com.srap.nga.ui.topic.subject.TopicSubjectScreen
import com.srap.nga.ui.userinfo.UserInfoScreen
import com.srap.nga.ui.webview.WebViewScreen
import com.srap.nga.logic.session.SessionManager
import com.srap.nga.logic.session.SessionNavigationEvent
import org.json.JSONArray

@Composable
fun AppNavigation(
    navController: NavHostController,
    sessionManager: SessionManager,
) {
    val userId by sessionManager.userId.collectAsState()
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState()

    LaunchedEffect(navController) {
        sessionManager.navigationEvents.collect { event ->
            when (event) {
                SessionNavigationEvent.Home -> navController.navigateToHome()
                SessionNavigationEvent.Login -> navController.navigateToLogin()
            }
        }
    }

    NavHost(
        navController,
        startDestination = "home",
        enterTransition = {
            // 向左滑入
            slideIn { IntOffset((0.1 * it.width).toInt(), 0) } + fadeIn()
        },
        exitTransition = {
            // 向右滑出
            slideOut { IntOffset(-(0.1 * it.width).toInt(), 0) } + fadeOut()
        },
        popEnterTransition = {
            // 向右滑入
            slideIn { IntOffset(-(0.1 * it.width).toInt(), 0) } + fadeIn()
        },
        popExitTransition = {
            // 向左滑出
            slideOut { IntOffset((0.1 * it.width).toInt(), 0) } + fadeOut()
        }
    ) {
        composable("home") {
            MainScreen(
                userId = userId,
                isLoggedIn = isLoggedIn,
                onViewPost = navController::navigateToPost,
                onViewTopicSubject = navController::navigateToTopicSubject,
                onSearch = navController::navigateToSearch,
                onViewLogin = navController::navigateToLogin,
                onViewFavorite = navController::navigateToFavorite,
                onViewSettings = navController::navigateToSettings,
                openUrl = navController::openUrl,
            )
        }

        // 帖子详细页
        composable(
            "post/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                },
            ),
        ) {
            val id = it.arguments?.getInt("id")
            if (id != null) {
                PostScreen(
                    id = id,
                    onBackClick = navController::popBackStack,
                    onViewPost = navController::navigateToPost,
                    onViewTopicSubject = navController::navigateToTopicSubject,
                    onUserInfo = navController::navigateToUserInfo,
                    isLoggedIn = isLoggedIn,
                    onViewLogin = navController::navigateToLogin,
                    openUrl = navController::openUrl,
                )
            } else {
                Text("帖子ID为空")
            }
        }

        // 社区详细页
        composable(
            "topic/subject?id={id}&isFavor={isFavor}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                },
                navArgument("isFavor") {
                    type = NavType.StringType
                }
            ),
        ) {
            val id = it.arguments?.getInt("id")
            val isFavorString = it.arguments?.getString("isFavor")
            val isFavor = when (isFavorString) {
                "true" -> true
                "false" -> false
                else -> null
            }
            if (id != null) {
                TopicSubjectScreen(
                    id=id,
                    onBackClick = navController::popBackStack,
                    onViewPost = navController::navigateToPost,
                    isFavor = isFavor
                )
            } else {
                Text("社区ID为空")
            }
        }

        // 图片预览
        composable(
            "image/preview?image={image}&images={images}",
            arguments = listOf(
                navArgument("image") {
                    type = NavType.StringType
                },
                navArgument("images") {
                    type = NavType.StringType
                },
            ),
        ) {
            val image = it.arguments?.getString("image")
            val images = it.arguments
                ?.getString("images")
                ?.let { value ->
                    runCatching {
                        val json = JSONArray(value)
                        List(json.length()) { index -> json.getString(index) }
                    }.getOrNull()
                }
            if (image != null && images != null) {
                ImagePreviewScreen(
                    image=image,
                    images=images
                )
            } else {
                Text("图片数据异常")
            }
        }

        // 用户详细页
        composable(
            "userinfo/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                },
            ),
        ) {
            val id = it.arguments?.getInt("id")
            if (id != null) {
                UserInfoScreen(
                    id = id,
                    isLoggedIn = isLoggedIn,
                    onViewPost = navController::navigateToPost,
                    onViewLogin = navController::navigateToLogin,
                    onBackClick = navController::popBackStack,
                )
            } else {
                Text("用户ID为空")
            }
        }

        // 扫码登录
        composable(
            "login/qrcode"
        ) {
            QRCodeLoginScreen(
                onBackClick = navController::popBackStack
            )
        }

        // 搜索页
        composable(
            "search"
        ) {
            SearchScreen(
                onBackClick = navController::popBackStack,
                onViewSearchResult = navController::navigateToSearchResult,
            )
        }

        // 搜索页
        composable(
            "search/result/{key}",
            arguments = listOf(
                navArgument("key") {
                    type = NavType.StringType
                },
            ),
        ) {
            val key = it.arguments?.getString("key")
            if (key != null) {
                SearchResultScreen(
                    key = key,
                    onViewPost = navController::navigateToPost,
                    onViewTopicSubject = navController::navigateToTopicSubject,
                    onBackClick = navController::popBackStack,
                )
            } else {
                Text("搜索字为空为空")
            }
        }

        // 收藏夹
        composable(
            "favorite"
        ) {
            FavoriteScreen(
                onViewFavoriteContent = navController::navigateToFavoriteContent,
                onBackClick = navController::popBackStack,
            )
        }

        // 收藏夹内容
        composable(
            "favorite/content/{id}/{title}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                },
                navArgument("title") {
                    type = NavType.StringType
                },
            ),
        ) {
            val id = it.arguments?.getInt("id")
            val title = it.arguments?.getString("title")
            if (id != null && title != null){
                FavoriteContentScreen(
                    id = id,
                    title = title,
                    onViewPost = navController::navigateToPost,
                    onBackClick = navController::popBackStack,
                )
            } else {
                Text("收藏ID或名称为空")
            }
        }

        // 设置
        composable("settings") {
            SettingsScreen(
                onBackClick = navController::popBackStack,
            )
        }

        // 打开URL
        composable(
            "webview/{url}",
            arguments = listOf(
                navArgument("url") {
                    type = NavType.StringType
                }
            )
        ) {
            val url = it.arguments?.getString("url")
            if (url != null) {
                WebViewScreen(
                    url = url,
                    onBackClick = navController::popBackStack,
                )
            } else {
                Text("链接为空")
            }
        }
    }
}

fun NavHostController.navigateToHome() {
    navigate("home") {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
}

fun NavHostController.navigateToPost(id: Int) {
    navigate("post/$id")
}

fun NavHostController.navigateToTopicSubject(id: Int, isFavor: Boolean?) {
    var isFavorString = isFavor.toString()
    if (isFavor == null) {
        isFavorString = "unset"
    }
    navigate("topic/subject?id=${id}&isFavor=${isFavorString}")
}

fun NavHostController.navigateToUserInfo(id: Int) {
    navigate("userinfo/$id")
}

fun NavHostController.navigateToLogin() {
    navigate("login/qrcode") {
        popUpTo("home")
    }
}

fun NavHostController.navigateToSearch() {
    navigate("search")
}

fun NavHostController.navigateToSearchResult(key: String) {
    val normalizedKey = key.trim()
    if (normalizedKey.isNotEmpty()) {
        navigate("search/result/${Uri.encode(normalizedKey)}")
    }
}

fun NavHostController.navigateToImagePreView(image: String, images: List<String>) {
    val encodedImages = Uri.encode(JSONArray(images).toString())
    navigate("image/preview?image=${Uri.encode(image)}&images=$encodedImages")
}

fun NavHostController.navigateToFavorite() {
    navigate("favorite")
}

fun NavHostController.navigateToSettings() {
    navigate("settings")
}

fun NavHostController.navigateToFavoriteContent(id: Int, title: String) {
    navigate("favorite/content/$id/${Uri.encode(title)}")
}

fun NavHostController.openUrl(url: String) {
    navigate("webview/${Uri.encode(url)}")
}
