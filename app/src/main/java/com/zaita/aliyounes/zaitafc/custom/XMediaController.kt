package com.zaita.aliyounes.zaitafc.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.MediaController

/**
 * Created by ali.younes on 1/25/2017.
 */

class XMediaController : MediaController {
    private var listener: OnDisplayStateChangedListener? = null
    private val isDefaultPlay: Boolean = false

    constructor(context: Context) : super(context) {
        this.listener = null
    }

    constructor(context: Context, useForwardBack: Boolean) : super(context, useForwardBack) {}

    interface OnDisplayStateChangedListener {
        fun onShow()
        fun onHide()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.listener = null
    }

    fun setOnDisplayStateChangedListener(listener: OnDisplayStateChangedListener) {
        this.listener = listener
    }

    override fun show() {
        if (listener != null)
            listener!!.onShow()
        super.show()
    }

    override fun hide() {
        if (listener != null)
            listener!!.onHide()
        super.hide()
    }
}
