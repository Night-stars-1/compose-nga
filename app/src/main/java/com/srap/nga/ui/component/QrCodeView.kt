package com.srap.nga.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.srap.nga.utils.QRCodeUtil.generateQRCode

@Composable
fun QrCodeView(
    data: String,
    modifier: Modifier = Modifier
) {
    val qrBitmap = remember(data) { generateQRCode(data) }

    if (qrBitmap != null) {
        Image(
            bitmap = qrBitmap.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = modifier.size(200.dp)
        )
    }
}