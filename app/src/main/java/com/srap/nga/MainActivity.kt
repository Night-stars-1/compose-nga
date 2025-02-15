package com.srap.nga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.srap.nga.ui.AppNavigation
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.srap.nga.ui.image.ImagePreviewTransformScreen
import com.srap.nga.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            navController = rememberNavController()

            AppTheme(
                dynamicColor = false
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
//                .windowInsetsPadding(ScaffoldDefaults.contentWindowInsets)
                ) {
//                    PermissionDeniedDialog()
                    Box {
                        AppNavigation(navController)
                        ImagePreviewTransformScreen()
                    }
                }
            }
        }
    }
}
