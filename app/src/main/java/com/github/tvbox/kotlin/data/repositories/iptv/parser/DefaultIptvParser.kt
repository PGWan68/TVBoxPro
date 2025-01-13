package com.github.tvbox.kotlin.data.repositories.iptv.parser

import com.github.tvbox.kotlin.data.entities.Iptv
import com.github.tvbox.kotlin.data.entities.IptvGroup
import com.github.tvbox.kotlin.data.entities.IptvGroupList
import com.github.tvbox.kotlin.data.entities.IptvList

class DefaultIptvParser : IptvParser {

    override fun isSupport(url: String, data: String): Boolean {
        return true
    }

    override suspend fun parse(data: String): IptvGroupList {
        return IptvGroupList(
            listOf(
                IptvGroup(
                    name = "不支持当前直播源链接格式，请切换其他直播源链接；支持的直播源链接格式：m3u、tvbox",
                    iptvList = IptvList(
                        listOf(
                            Iptv(name = "m3u", channelName = "m3u", urlList = listOf()),
                            Iptv(name = "tvbox", channelName = "tvbox", urlList = listOf()),
                        )
                    )
                )
            )
        )
    }
}