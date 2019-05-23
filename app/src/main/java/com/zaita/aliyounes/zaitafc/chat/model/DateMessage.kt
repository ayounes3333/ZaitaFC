package com.zaita.aliyounes.zaitafc.chat.model

import com.zaita.aliyounes.zaitafc.helpers.DateTimeUtils

import java.text.SimpleDateFormat
import java.util.Locale

class DateMessage : Message() {
    var date: String? = null

    init {
        val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        this.date = simpleDateFormat.format(DateTimeUtils.todayDate)
    }
}
