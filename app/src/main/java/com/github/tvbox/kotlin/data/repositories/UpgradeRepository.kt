package com.github.tvbox.kotlin.data.repositories

import com.github.tvbox.kotlin.data.entities.UpdateRelease
import com.github.tvbox.kotlin.utils.Loggable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject

class UpgradeRepository : Loggable() {

    val url = "https://www.pgyer.com/apiv2/app/check"
    val apiKey = "e5b445589e63283198ede7adebfd5aa6"
    val userKey = "fed1fb290ad7c29e49a3a43ad1351c79"
    val appKey = "11e6a182aa9c43f25c050389cb1daf42"

    suspend fun latestRelease() = withContext(Dispatchers.IO) {
        log.d("获取最新版本: $url")

        val params = HashMap<String, Any>()
        params.put("_api_key", apiKey)
        params.put("appKey", appKey)
//        params.put("buildVersion",co)

        val client = OkHttpClient()
        val request = Request.Builder().url(url).post(
            RequestBody.create(
                MediaType.parse("application/x-www-form-urlencoded"),
                JSONObject(params).toString()
            )
        ).build()

        try {
            with(client.newCall(request).execute()) {
                if (!isSuccessful) {
                    throw Exception("获取最新发行版失败: ${code()}")
                }


                val data = body()!!.string();

                log.d("获取最新版本成功 data : $data")

                val json = Json.parseToJsonElement(body()!!.string()).jsonObject

                log.d("获取最新版本成功: $json")

                return@with UpdateRelease(
                    buildHaveNewVersion = json.getValue("buildHaveNewVersion").jsonPrimitive.boolean,
                    downloadURL = json.getValue("downloadURL").jsonPrimitive.content,
                    version = json.getValue("buildVersion").jsonPrimitive.content,
                    buildUpdateDescription = json.getValue("buildUpdateDescription").jsonPrimitive.content,
                )
            }
        } catch (ex: Exception) {
            log.e("获取最新版本失败", ex)
            throw Exception("获取最新版本失败，请检查网络连接", ex)
        }
    }


    fun getRelease(){
        log.d("获取最新版本: $url")

        val params = HashMap<String, Any>()
        params.put("_api_key", apiKey)
        params.put("appKey", appKey)
//        params.put("buildVersion",co)

        val client = OkHttpClient()
        val request = Request.Builder().url(url).post(
            RequestBody.create(
                MediaType.parse("application/x-www-form-urlencoded"),
                JSONObject(params).toString()
            )
        ).build()

        log.d("获取最新版本成功 params : ${JSONObject(params)}")


        try {
            with(client.newCall(request).execute()) {
                if (!isSuccessful) {
                    throw Exception("获取最新发行版失败: ${code()}")
                }

                val data = body()!!.string();

                log.d("获取最新版本成功 data : $data")


                val json = Json.parseToJsonElement(body()!!.string()).jsonObject

                log.d("获取最新版本成功: $json")

                return@with UpdateRelease(
                    buildHaveNewVersion = json.getValue("buildHaveNewVersion").jsonPrimitive.boolean,
                    downloadURL = json.getValue("downloadURL").jsonPrimitive.content,
                    version = json.getValue("buildVersion").jsonPrimitive.content,
                    buildUpdateDescription = json.getValue("buildUpdateDescription").jsonPrimitive.content,
                )
            }
        } catch (ex: Exception) {
            log.e("获取最新版本失败", ex)
            throw Exception("获取最新版本失败，请检查网络连接", ex)
        }
    }
}