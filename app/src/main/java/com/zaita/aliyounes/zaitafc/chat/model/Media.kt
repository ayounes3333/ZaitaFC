package com.zaita.aliyounes.zaitafc.chat.model

import java.util.UUID

/**
 * Created by Lenovo on 5/1/2018.
 */

/*
 every variable in this class will be a JSON field
 so when we create this class we should pay attention to the
 database structure and field names
 */

class Media {
    var id = UUID.randomUUID().toString()
    var path: String = ""
    @Suppress("MemberVisibilityCanBePrivate")
    var uploadedBy: String = ""
    var type: Int = 0

    object MediaTypes {
        const val IMAGE = 1
        const val VIDEO = 2
        const val ADD = 3
    }

    constructor(type: Int) {
        this.type = type
    }

    constructor(path: String, uploadedBy: String, type: Int) {
        this.path = path
        this.uploadedBy = uploadedBy
        this.type = type
    }
}
