package com.zaita.aliyounes.zaitafc.helpers.glideprogress

import android.content.Context
import android.os.Handler
import android.os.Looper

import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule

import java.io.IOException
import java.io.InputStream
import java.util.WeakHashMap

import okhttp3.HttpUrl
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Okio
import okio.Source

@GlideModule
class ProgressAppGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        val client = OkHttpClient.Builder()
                .addNetworkInterceptor { chain ->
                    val request = chain.request()
                    val response = chain.proceed(request)
                    val listener = DispatchingProgressListener()
                    response.newBuilder()
                            .body(OkHttpProgressResponseBody(request.url(), response.body()!!, listener))
                            .build()
                }
                .build()
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))
    }

    private interface ResponseProgressListener {
        fun update(url: HttpUrl, bytesRead: Long, contentLength: Long)
    }

    interface UIonProgressListener {
        /**
         * Control how often the listener needs an update. 0% and 100% will always be dispatched.
         * @return in percentage (0.2 = call [.onProgress] around every 0.2 percent of progress)
         */
        val granualityPercentage: Float

        fun onProgress(bytesRead: Long, expectedLength: Long)
    }

    private class DispatchingProgressListener internal constructor() : ProgressAppGlideModule.ResponseProgressListener {

        private val handler: Handler = Handler(Looper.getMainLooper())

        override fun update(url: HttpUrl, bytesRead: Long, contentLength: Long) {
            //System.out.printf("%s: %d/%d = %.2f%%%n", url, bytesRead, contentLength, (100f * bytesRead) / contentLength);
            val key = url.toString()
            val listener = LISTENERS[key] ?: return
            if (contentLength <= bytesRead) {
                forget(key)
            }
            if (needsDispatch(key, bytesRead, contentLength, listener.granualityPercentage)) {
                handler.post { listener.onProgress(bytesRead, contentLength) }
            }
        }

        private fun needsDispatch(key: String, current: Long, total: Long, granularity: Float): Boolean {
            if (granularity == 0f || current == 0L || total == current) {
                return true
            }
            val percent = 100f * current / total
            val currentProgress = (percent / granularity).toLong()
            val lastProgress = PROGRESSES[key]
            return if (lastProgress == null || currentProgress != lastProgress) {
                PROGRESSES[key] = currentProgress
                true
            } else {
                false
            }
        }

        companion object {
            private val LISTENERS = WeakHashMap<String, UIonProgressListener>()
            private val PROGRESSES = WeakHashMap<String, Long>()

            internal fun forget(url: String) {
                LISTENERS.remove(url)
                PROGRESSES.remove(url)
            }

            internal fun expect(url: String, listener: UIonProgressListener) {
                LISTENERS[url] = listener
            }
        }
    }

    private class OkHttpProgressResponseBody internal constructor(private val url: HttpUrl, private val responseBody: ResponseBody,
                                                                  private val progressListener: ResponseProgressListener) : ResponseBody() {
        private var bufferedSource: BufferedSource? = null

        override fun contentType(): MediaType? {
            return responseBody.contentType()
        }

        override fun contentLength(): Long {
            return responseBody.contentLength()
        }

        override fun source(): BufferedSource {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()))
            }
            return bufferedSource!!
        }

        private fun source(source: Source): Source {
            return object : ForwardingSource(source) {
                var totalBytesRead = 0L

                @Throws(IOException::class)
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    val fullLength = responseBody.contentLength()
                    if (bytesRead == -1L) { // this source is exhausted
                        totalBytesRead = fullLength
                    } else {
                        totalBytesRead += bytesRead
                    }
                    progressListener.update(url, totalBytesRead, fullLength)
                    return bytesRead
                }
            }
        }
    }

    companion object {

        fun forget(url: String) {
            ProgressAppGlideModule.DispatchingProgressListener.forget(url)
        }

        fun expect(url: String, listener: ProgressAppGlideModule.UIonProgressListener) {
            ProgressAppGlideModule.DispatchingProgressListener.expect(url, listener)
        }
    }
}