package com.github.tvbox.kotlin.ui.screens.leanback.components

import androidx.compose.runtime.Composable

@Composable
fun LeanbackVisible(
    visibleProvider: () -> Boolean = { false },
    content: @Composable () -> Unit
) {
    if (visibleProvider()) {
        content()
    }
}