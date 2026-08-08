package com.srap.nga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.srap.nga.ui.AppNavigation
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.srap.nga.ui.image.ImagePreviewTransformScreen
import com.srap.nga.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import com.srap.nga.logic.session.SessionManager
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    @Inject lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            navController = rememberNavController()

            AppTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
//                .windowInsetsPadding(ScaffoldDefaults.contentWindowInsets)
                ) {
//                    PermissionDeniedDialog()
                    Box {
                        AppNavigation(navController, sessionManager)
                        ImagePreviewTransformScreen()
                    }
                }
            }
        }
    }
}
