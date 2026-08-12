package com.srap.nga.ui.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.srap.nga.logic.model.GithubReleaseResponse

/**
 * 发现新版本对话框
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppUpdateDialog(
    release: GithubReleaseResponse,
    isDownloading: Boolean,
    downloadProgress: Int,
    onDownload: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val openInBrowser = {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, release.downloadUrl.toUri())
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "发现新版本 ${release.tagName}",
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 320.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    val body = release.body?.trim().orEmpty()
                    if (body.isEmpty()) {
                        Text(
                            text = "暂无更新说明",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        val bodyMedium = MaterialTheme.typography.bodyMedium
                        Markdown(
                            content = body,
                            typography = markdownTypography(
                                h1 = MaterialTheme.typography.titleMedium,
                                h2 = MaterialTheme.typography.titleMedium,
                                h3 = MaterialTheme.typography.titleSmall,
                                h4 = MaterialTheme.typography.titleSmall,
                                h5 = MaterialTheme.typography.titleSmall,
                                h6 = MaterialTheme.typography.titleSmall,
                                text = bodyMedium,
                                paragraph = bodyMedium,
                                ordered = bodyMedium,
                                bullet = bodyMedium,
                                list = bodyMedium,
                                quote = bodyMedium,
                            ),
                        )
                    }
                }
                if (isDownloading) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LinearWavyProgressIndicator(
                            progress = { downloadProgress / 100f },
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "$downloadProgress%",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onDismiss) {
                    Text("取消")
                }
                Spacer(modifier = Modifier.weight(1f))
                if (release.apkDownloadUrl != null) {
                    IconTextButton(
                        icon = Icons.Outlined.OpenInBrowser,
                        text = "浏览器打开",
                        onClick = openInBrowser,
                    )
                    IconTextButton(
                        icon = Icons.Outlined.Download,
                        text = "安装",
                        onClick = onDownload,
                        enabled = !isDownloading,
                    )
                } else {
                    IconTextButton(
                        icon = Icons.Outlined.OpenInBrowser,
                        text = "前往下载",
                        onClick = openInBrowser,
                    )
                }
            }
        },
    )
}

@Composable
private fun IconTextButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        contentPadding = ButtonDefaults.TextButtonWithIconContentPadding,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(ButtonDefaults.IconSize),
        )
        Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
        Text(text)
    }
}
