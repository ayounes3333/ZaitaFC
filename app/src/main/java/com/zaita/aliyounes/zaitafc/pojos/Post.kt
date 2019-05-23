package com.zaita.aliyounes.zaitafc.pojos

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ali.Younes on 4/14/2017.
 */

class Post {
    var title = ""
    var body = ""
    var creationDate = ""
    var type = 0
    var mediaURL = ""
    var mediaCount = 0
    var mediaURLs: ArrayList<String> = ArrayList()

    constructor()

    constructor(title: String, body: String, type: Int, creationDate: String) {
        this.title = title
        this.body = body
        this.type = type
        this.creationDate = creationDate
    }

    constructor(title: String, body: String, type: Int, creationDate: String, mediaURLs: ArrayList<String>) {
        this.title = title
        this.body = body
        this.type = type
        this.creationDate = creationDate
        this.mediaURLs = mediaURLs
    }

    constructor(title: String, body: String, type: Int, mediaURL: String, creationDate: String) {
        this.title = title
        this.body = body
        this.type = type
        this.mediaURL = mediaURL
        this.creationDate = creationDate
    }

    companion object {

        @Suppress("unused")
        private val postDateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm", Locale.ENGLISH)
    }
}
