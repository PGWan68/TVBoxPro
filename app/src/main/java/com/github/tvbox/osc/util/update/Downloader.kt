package com.github.tvbox.osc.util.update

import com.github.tvbox.osc.subtitle.runtime.AppTaskExecutor
import com.github.tvbox.osc.subtitle.runtime.TaskExecutor
import com.github.tvbox.osc.util.LOG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer
import java.io.File
import java.io.FileOutputStream

object Downloader {

    fun downloadApk(url: String, filePath: String, callback: Callback?) {

        LOG.i("下载文件: $url")
        callback?.onStart();

        AppTaskExecutor.getInstance().executeOnDeskIO {
            val interceptor = Interceptor { chain ->
                val originalResponse = chain.proceed(chain.request())
                originalResponse.newBuilder().body(DownloadResponseBody(originalResponse, callback))
                    .build()
            }

            val client = OkHttpClient.Builder().addNetworkInterceptor(interceptor).build()
            val request = okhttp3.Request.Builder().url(url).build()

            try {

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    callback?.onError("下载文件失败: ${response.code()}")
                } else {
                    val file = File(filePath)
                    FileOutputStream(file).use { fos -> fos.write(response.body()!!.bytes()) }
                }

                AppTaskExecutor.getInstance().executeOnMainThread { callback?.onFinish(); }

            } catch (ex: Exception) {
                LOG.e("下载文件失败", ex)
                AppTaskExecutor.getInstance()
                    .executeOnMainThread {
                        callback?.onError("下载文件失败，请检查网络连接")
                        callback?.onFinish()
                    }

            }
        }
    }


    interface Callback {
        fun onStart() {}
        fun onProgress(progress: Int) {}
        fun onFinish() {}
        fun onError(msg: String) {}
    }

    private class DownloadResponseBody(
        private val originalResponse: okhttp3.Response,
        private val callback: Callback?,
    ) : okhttp3.ResponseBody() {

        var lastProgress = 0;

        override fun contentLength() = originalResponse.body()!!.contentLength()

        override fun contentType() = originalResponse.body()?.contentType()

        override fun source(): BufferedSource {
            return object : ForwardingSource(originalResponse.body()!!.source()) {
                var totalBytesRead = 0L


                override fun read(sink: okio.Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    totalBytesRead += if (bytesRead != -1L) bytesRead else 0
                    val progress = (totalBytesRead * 100 / contentLength()).toInt()
                    CoroutineScope(Dispatchers.IO).launch {
                        if (lastProgress != progress) {

                            AppTaskExecutor.getInstance()
                                .executeOnMainThread { callback?.onProgress(progress) }

                            lastProgress = progress;
                        }
                    }
                    return bytesRead
                }
            }.buffer()
        }
    }
}