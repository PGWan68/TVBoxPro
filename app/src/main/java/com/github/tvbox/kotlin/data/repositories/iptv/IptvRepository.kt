package com.github.tvbox.kotlin.data.repositories.iptv

import com.github.tvbox.kotlin.data.entities.Iptv
import com.github.tvbox.kotlin.data.entities.IptvGroup
import com.github.tvbox.kotlin.data.entities.IptvGroupList
import com.github.tvbox.kotlin.data.entities.IptvList
import com.github.tvbox.kotlin.data.repositories.FileCacheRepository
import com.github.tvbox.kotlin.data.repositories.iptv.parser.IptvParser
import com.github.tvbox.kotlin.utils.Logger
import com.github.tvbox.osc.util.LOG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * 直播源获取
 */
class IptvRepository : FileCacheRepository("iptv.txt") {
    private val log = Logger.create(javaClass.simpleName)

    /**
     * 获取远程直播源数据
     */
    private suspend fun fetchSource(sourceUrl: String) = withContext(Dispatchers.IO) {
        log.d("获取远程直播源: $sourceUrl")

        val client = OkHttpClient()
        val request = Request.Builder().url(sourceUrl).build()

        try {
            with(client.newCall(request).execute()) {
                if (!isSuccessful) {
                    throw Exception("获取远程直播源失败: ${code()}")
                }

                return@with body()!!.string()
            }
        } catch (ex: Exception) {
            log.e("获取远程直播源失败", ex)
            throw Exception("获取远程直播源失败，请检查网络连接", ex)
        }
    }

    /**
     * 简化规则
     */
    private fun simplifyTest(group: IptvGroup, iptv: Iptv): Boolean {
        return iptv.name.lowercase().startsWith("cctv") || iptv.name.endsWith("卫视")
    }

    /**
     * 获取直播源分组列表
     */
    suspend fun getIptvGroupList(
        sourceUrl: String,
        cacheTime: Long,
        simplify: Boolean = false,
    ): IptvGroupList {
        try {
            val sourceData = getOrRefresh(cacheTime) {
                fetchSource(sourceUrl)
            }

            val parser = IptvParser.instances.first { it.isSupport(sourceUrl, sourceData) }
            val groupList = parser.parse(sourceData)
            log.i("解析直播源完成：${groupList.size}个分组，${groupList.flatMap { it.iptvList }.size}个频道")

            LOG.i("分组：${groupList.map { it.name }}")
            LOG.i("频道：${groupList.flatMap { it.iptvList }}")

            if (simplify) {
                return IptvGroupList(groupList.map { group ->
                    IptvGroup(
                        name = group.name, iptvList = IptvList(group.iptvList.filter { iptv ->
                            simplifyTest(group, iptv)
                        })
                    )
                }.filter { it.iptvList.isNotEmpty() })
            }

            return groupList
        } catch (ex: Exception) {
            log.e("获取直播源失败", ex)
            throw Exception(ex)
        }
    }
}