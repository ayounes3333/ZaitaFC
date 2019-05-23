package com.zaita.aliyounes.zaitafc.helpers

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri

import java.io.ByteArrayOutputStream
import java.util.HashMap

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object ImageUtils {

    // get bitmap thumbnail from online video
    @Throws(Throwable::class)
    private fun retrieveVideoFrameFromVideo(videoPath: String): Bitmap {
        val bitmap: Bitmap
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()

            mediaMetadataRetriever.setDataSource(videoPath, HashMap())


            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable(
                    "Exception in retrieveVideoFrameFromVideo(String videoPath)" + e.message)

        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }

    // get thumbnail from online video as bytes array and asynchronously with rxJava
    fun getVideoThumbnailRx(path: Uri): Observable<ByteArray> {
        return Observable.create(ObservableOnSubscribe<ByteArray> { emitter ->
            try {
                val image = ImageUtils.retrieveVideoFrameFromVideo(path.toString())
                val stream = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.PNG, 100, stream)
                emitter.onNext(stream.toByteArray())
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                emitter.onError(throwable)
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())

    }
}
