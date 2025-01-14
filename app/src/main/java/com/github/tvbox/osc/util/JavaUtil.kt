package com.github.tvbox.osc.util

import com.github.tvbox.osc.bean.LiveChannelGroup
import com.orhanobut.hawk.Hawk

/**
 *Automatic generated
 *@author Created by
 *@date 18/6/2024 19:11
 */
object JavaUtil {
    @JvmStatic
    fun findLiveLastChannel(liveChannelGroupList: List<LiveChannelGroup>): Pair<Int, Int> {
        val lastChannelName = Hawk.get(HawkConfig.LIVE_CHANNEL, "")
        val lastChannelGroupName = Hawk.get(HawkConfig.LIVE_CHANNEL_GROUP, "");
        return liveChannelGroupList.find { it.groupName == lastChannelGroupName }?.let {
            val channelItem =
                it.liveChannels.find { channelItem -> channelItem.channelName == lastChannelName }
            //如果该分组未查询到那么 走全局搜索
            if (channelItem == null) null else it.groupIndex to channelItem.channelIndex
        } ?: let {
            var noPassWordGroupIndex = -1
            liveChannelGroupList.forEach {
                it.liveChannels.forEach { liceChannel ->
                    if (liceChannel.channelName == lastChannelName) {
                        return it.groupIndex to liceChannel.channelIndex
                    }
                    if (noPassWordGroupIndex == -1 && it.groupPassword.isEmpty()) {
                        noPassWordGroupIndex = it.groupIndex
                    }
                }
            }
            if (noPassWordGroupIndex == -1) noPassWordGroupIndex = 0
            noPassWordGroupIndex to 0
        }
    }


}