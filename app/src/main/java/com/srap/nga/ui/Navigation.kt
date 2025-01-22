package com.srap.nga.ui

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.srap.nga.ui.image.ImagePreviewScreen
import com.srap.nga.ui.login.qrcode.QRCodeLoginScreen
import com.srap.nga.ui.main.MainScreen
import com.srap.nga.ui.post.PostScreen
import com.srap.nga.ui.topic.subject.TopicSubjectScreen
import com.srap.nga.ui.userinfo.UserInfoScreen
import com.srap.nga.utils.GlobalObject

@Composable
fun AppNavigation(navController: NavHostController) {
    GlobalObject.navController = navController

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
                onViewPost = navController::navigateToPost,
                onViewTopicSubject = navController::navigateToTopicSubject
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
                    onUserInfo = navController::navigateToUserInfo
                )
            } else {
                Text("帖子ID为空")
            }
        }

        // 社区详细页
        composable(
            "topic/subject/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.IntType
                },
            ),
        ) {
            val id = it.arguments?.getInt("id")
            if (id != null) {
                TopicSubjectScreen(
                    id=id,
                    onBackClick = navController::popBackStack,
                    onViewPost = navController::navigateToPost,
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
                    type = NavType.StringListType
                },
            ),
        ) {
            val image = it.arguments?.getString("image")
            val images = NavType.StringListType[it.arguments!!, "images"]
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
                    onBackClick = navController::popBackStack,
                    onViewPost = navController::navigateToPost
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

fun NavHostController.navigateToTopicSubject(id: Int) {
    navigate("topic/subject/$id")
}

fun NavHostController.navigateToUserInfo(id: Int) {
    navigate("userinfo/$id")
}

fun NavHostController.navigateToLogin() {
    navigate("login/qrcode") {
        popUpTo("home")
    }
}

fun NavHostController.navigateToImagePreView(image: String, images: List<String>) {
    navigate("image/preview?image=$image&images=${images.joinToString("&images=")}")
}
