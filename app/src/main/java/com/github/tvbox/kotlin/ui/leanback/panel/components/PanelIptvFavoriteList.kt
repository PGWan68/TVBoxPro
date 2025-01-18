package com.github.tvbox.kotlin.ui.leanback.panel.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import com.github.tvbox.kotlin.data.entities.Epg
import com.github.tvbox.kotlin.data.entities.Epg.Companion.currentProgrammes
import com.github.tvbox.kotlin.data.entities.EpgList
import com.github.tvbox.kotlin.data.entities.Iptv
import com.github.tvbox.kotlin.data.entities.IptvList
import com.github.tvbox.kotlin.ui.rememberLeanbackChildPadding
import com.github.tvbox.kotlin.ui.theme.LeanbackTheme
import com.github.tvbox.kotlin.ui.utils.handleLeanbackKeyEvents
import kotlin.math.max
import kotlin.math.min

@Composable
fun LeanbackPanelIptvFavoriteList(
    modifier: Modifier = Modifier,
    iptvListProvider: () -> IptvList = { IptvList() },
    epgListProvider: () -> EpgList = { EpgList() },
    currentIptvProvider: () -> Iptv = { Iptv() },
    showProgrammeProgressProvider: () -> Boolean = { false },
    onIptvSelected: (Iptv) -> Unit = {},
    onIptvFavoriteToggle: (Iptv) -> Unit = {},
    onClose: () -> Unit = {},
    onUserAction: () -> Unit = {},
) {
    val favoriteListSize = 6
    val childPadding = rememberLeanbackChildPadding()

    var key by remember { mutableIntStateOf(0) }
    val iptvList = remember(key) { iptvListProvider() }
    val listState = rememberLazyGridState(max(0, iptvList.indexOf(currentIptvProvider())))

    var hasFocused by rememberSaveable { mutableStateOf(false) }

    var showEpgDialog by remember { mutableStateOf(false) }
    var currentShowEpgIptv by remember { mutableStateOf(Iptv()) }

    LaunchedEffect(iptvList) {
        if (iptvList.isEmpty()) onClose()
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { _ -> onUserAction() }
    }

    Column(modifier = modifier) {
        Row(modifier = Modifier.padding(start = childPadding.start)) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.labelMedium,
            ) {
                Text(text = "收藏")
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${iptvList.size}个频道",
                    color = LocalContentColor.current.copy(alpha = 0.8f),
                )
            }
        }

        LazyVerticalGrid(
            state = listState,
            columns = GridCells.Fixed(favoriteListSize),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(
                top = 6.dp,
                start = childPadding.start,
                end = childPadding.end,
                bottom = childPadding.bottom,
            ),
        ) {
            itemsIndexed(iptvList) { index, iptv ->
                LeanbackPanelIptvItem(
                    modifier = if (index < favoriteListSize) {
                        Modifier.Companion.handleLeanbackKeyEvents(onUp = { onClose() })
                    } else Modifier,
                    iptvProvider = { iptv },
                    currentProgrammeProvider = {
                        epgListProvider().firstOrNull { epg -> epg.channel == iptv.channelName }
                            ?.currentProgrammes()?.now
                    },
                    showProgrammeProgressProvider = { showProgrammeProgressProvider() },
                    onIptvSelected = { onIptvSelected(iptv) },
                    onIptvFavoriteToggle = {
                        key++
                        onIptvFavoriteToggle(iptv)
                    },
                    onShowEpg = {
                        currentShowEpgIptv = iptv
                        showEpgDialog = true
                    },
                    initialFocusedProvider = {
                        if (hasFocused) false
                        else if (index == 0 && !iptvList.contains(currentIptvProvider())) true
                        else iptv == currentIptvProvider()
                    },
                    onHasFocused = { hasFocused = true },
                )
            }
        }
    }

    LeanbackPanelIptvEpgDialog(
        showDialogProvider = { showEpgDialog },
        onDismissRequest = { showEpgDialog = false },
        iptvProvider = { currentShowEpgIptv },
        epgProvider = {
            epgListProvider().firstOrNull { epg ->
                epg.channel == currentShowEpgIptv.channelName
            } ?: Epg()
        },
        modifier = Modifier
            .handleLeanbackKeyEvents(
                onLeft = {
                    currentShowEpgIptv = iptvList[max(0, iptvList.indexOf(currentShowEpgIptv) - 1)]
                },
                onRight = {
                    currentShowEpgIptv =
                        iptvList[min(iptvList.size - 1, iptvList.indexOf(currentShowEpgIptv) + 1)]
                },
            ),
        onUserAction = onUserAction,
    )
}

@Preview(device = Devices.TV_1080p)
@Composable
private fun LeanbackPanelIptvFavoriteListPreview() {
    LeanbackTheme {
        LeanbackPanelIptvFavoriteList(
            iptvListProvider = { IptvList.EXAMPLE },
        )
    }
}