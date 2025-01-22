package com.srap.nga.ui.login.qrcode

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.srap.nga.logic.network.NetworkModule
import com.srap.nga.ui.component.QrCodeView
import com.srap.nga.ui.component.button.BackButton
import com.srap.nga.ui.component.card.LoadingCard
import com.srap.nga.ui.component.webview.openUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodeLoginScreen(
    onBackClick: () -> Unit
) {
    val viewModel: QRCodeLoginViewModel = hiltViewModel()

    val url = NetworkModule.NGA_QR_LOGIN_URL.format(viewModel.result?.qrKey)

    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets.systemBars
                    .only(
                        WindowInsetsSides.Top + WindowInsetsSides.Start
                    ),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    BackButton { onBackClick() }
                },
                title = {
                    Text("扫码登录")
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (viewModel.result == null) {
                LoadingCard()
            } else {
                QrCodeView(
                    data = url,
                    modifier = Modifier
                        .clickable {
                            openUrl(url)
                        }
                )
            }
        }
    }
}