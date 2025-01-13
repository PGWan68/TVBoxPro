package com.github.tvbox.kotlin.ui.leanback.update.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.Button
import com.github.tvbox.kotlin.data.entities.GitRelease
import com.github.tvbox.kotlin.ui.theme.LeanbackTheme
import com.github.tvbox.kotlin.ui.utils.handleLeanbackKeyEvents

@Composable
fun LeanbackUpdateDialog(
    modifier: Modifier = Modifier,
    showDialogProvider: () -> Boolean = { false },
    onDismissRequest: () -> Unit = {},
    releaseProvider: () -> GitRelease = { GitRelease() },
    onUpdateAndInstall: () -> Unit = {},
) {
    if (showDialogProvider()) {
        val release = releaseProvider()
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        AlertDialog(
            properties = DialogProperties(usePlatformDefaultWidth = false),
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            confirmButton = {
                Button(
                    onClick = {},
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .handleLeanbackKeyEvents(
                            onSelect = onUpdateAndInstall,
                        ),
                ) {
                    androidx.tv.material3.Text(text = "立即更新")
                }
            },
            dismissButton = {
                Button(
                    onClick = {},
                    modifier = Modifier.handleLeanbackKeyEvents(
                        onSelect = onDismissRequest,
                    ),
                ) {
                    androidx.tv.material3.Text(text = "忽略")
                }
            },
            title = {
                Text(text = "新版本：v${release.version}")
            },
            text = {
                LazyColumn {
                    item {
                        Text(text = release.description)
                    }
                }
            }
        )
    }
}

@Preview(device = "id:Android TV (720p)")
@Composable
private fun LeanbackUpdateDialogPreview() {
    LeanbackTheme {
        LeanbackUpdateDialog(
            showDialogProvider = { true },
            releaseProvider = {
                GitRelease(
                    version = "1.0.0",
                    description = "版本更新日志".repeat(100),
                )
            }
        )
    }
}