package com.github.tvbox.kotlin.ui.leanback.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.Text
import com.github.tvbox.kotlin.ui.leanback.settings.LeanbackSettingsCategories
import com.github.tvbox.kotlin.ui.theme.LeanbackTheme
import com.github.tvbox.kotlin.ui.utils.handleLeanbackKeyEvents

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LeanbackSettingsCategoryList(
    modifier: Modifier = Modifier,
    focusedCategoryProvider: () -> LeanbackSettingsCategories = { LeanbackSettingsCategories.entries.first() },
    onFocused: (LeanbackSettingsCategories) -> Unit = {},
) {
    var hasFocused = rememberSaveable { false }

    LazyColumn(
        contentPadding = PaddingValues(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.focusRestorer()
    ) {
        itemsIndexed(LeanbackSettingsCategories.entries) { index, category ->
            val isSelected by remember { derivedStateOf { focusedCategoryProvider() == category } }
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(Unit) {
                if (index == 0 && !hasFocused) {
                    focusRequester.requestFocus()
                    hasFocused = true
                }
            }

            LeanbackSettingsCategoryItem(
                modifier = Modifier.focusRequester(focusRequester),
                icon = category.icon,
                title = category.title,
                isSelectedProvider = { isSelected },
                onFocused = { onFocused(category) },
            )
        }
    }
}

@Composable
private fun LeanbackSettingsCategoryItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    isSelectedProvider: () -> Boolean = { false },
    onFocused: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    ListItem(
        selected = isSelectedProvider(),
        onClick = { },
        leadingContent = { Icon(icon, title) },
        headlineContent = { Text(text = title) },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused || it.hasFocus
                if (isFocused) {
                    onFocused()
                }
            }
            .handleLeanbackKeyEvents(
                onSelect = {
                    if (isFocused) focusManager.moveFocus(FocusDirection.Right)
                    else focusRequester.requestFocus()
                }
            ),
    )
}

@Preview
@Composable
private fun LeanbackSettingsCategoryItemPreview() {
    LeanbackTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            LeanbackSettingsCategoryItem(
                icon = LeanbackSettingsCategories.ABOUT.icon,
                title = LeanbackSettingsCategories.ABOUT.title,
            )

            LeanbackSettingsCategoryItem(
                icon = LeanbackSettingsCategories.ABOUT.icon,
                title = LeanbackSettingsCategories.ABOUT.title,
                isSelectedProvider = { true },
            )
        }
    }
}

@Preview
@Composable
private fun LeanbackSettingsCategoryListPreview() {
    LeanbackTheme {
        LeanbackSettingsCategoryList()
    }
}