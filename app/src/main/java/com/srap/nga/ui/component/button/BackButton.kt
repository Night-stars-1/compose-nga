package com.srap.nga.ui.component.button

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.srap.nga.utils.throttle

@Composable
fun BackButton(
    onBackClick: () -> Unit
) {
    IconButton(onClick = throttle {
        onBackClick()
    }) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "返回"
        )
    }
}