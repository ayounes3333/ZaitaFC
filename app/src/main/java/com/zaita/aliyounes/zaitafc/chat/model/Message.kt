package com.zaita.aliyounes.zaitafc.chat.model

open class Message {
    var senderId: String? = null
    var senderName: String? = null
    var messageDate: String? = null
    var chatId: String? = null
    var chatName: String? = null
    var messageContent: String? = null
    var contentType: Int = 0

    object ContentTypes {
        val TEXT = 1
        val IMAGE = 2
    }
}
