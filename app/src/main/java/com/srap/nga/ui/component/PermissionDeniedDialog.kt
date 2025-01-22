package com.srap.nga.ui.component

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun PermissionDeniedDialog() {
    val context = LocalContext.current // 获取 Context

    val permissionGranted = remember { mutableStateOf(false) }

    // 使用 rememberLauncherForActivityResult 注册权限请求
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("PermissionDeniedDialog", "通知权限已授予")
            permissionGranted.value = true
        } else {
            Log.i("PermissionDeniedDialog", "通知权限被拒绝")
            permissionGranted.value = false
        }
    }

    // 检查权限的逻辑
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(POST_NOTIFICATIONS)
            } else {
                Log.i("PermissionDeniedDialog", "通知权限已授予，无需再次请求")
                permissionGranted.value = true
            }
        } else {
            Log.i("PermissionDeniedDialog", "低于 Android 13，不需要请求通知权限")
            permissionGranted.value = true
        }
    }

    if (!permissionGranted.value) {
        AlertDialog(
            onDismissRequest = { permissionGranted.value = true },
            title = {
                Text(text = "请求通知权限", style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Text(
                    text = "应用需要通知权限以便为您提供通知服务，请前往设置开启权限。",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    navigateToAppSettings(context)
                    permissionGranted.value = true
                }) {
                    Text("去设置")
                }
            },
            dismissButton = {
                TextButton(onClick = { permissionGranted.value = true }) {
                    Text("取消")
                }
            }
        )
    }
}

// 导航到应用设置
private fun navigateToAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}