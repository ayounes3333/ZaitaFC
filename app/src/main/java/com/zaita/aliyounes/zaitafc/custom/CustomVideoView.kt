package com.zaita.aliyounes.zaitafc.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

import java.util.IllegalFormatException

/**
 * Created by aliyo on 22-Jan-17.
 */
class CustomVideoView : VideoView {

    private var mListener: PlayPauseListener? = null
    val percentPosition: Int
        get() = 100 * currentPosition / duration

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    fun setPlayPauseListener(listener: PlayPauseListener) {
        mListener = listener
    }

    override fun pause() {
        super.pause()
        if (mListener != null) {
            mListener!!.onPause()
        }
    }

    @Throws(IllegalFormatException::class)
    fun seekToPercent(percent: Int) {
        if (percent >= 0 && percent <= 100) {
            seekTo(percent * duration / 100)
        } else {
            throw IllegalArgumentException("Percentage modt be between 0 and 100")
        }
    }

    override fun start() {
        super.start()
        if (mListener != null) {
            mListener!!.onPlay()
        }
    }

    interface PlayPauseListener {
        fun onPlay()
        fun onPause()
    }

}
