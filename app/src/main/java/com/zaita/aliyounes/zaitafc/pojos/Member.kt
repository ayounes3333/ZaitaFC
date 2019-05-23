package com.zaita.aliyounes.zaitafc.pojos

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by Lenovo on 10/21/2017.
 */

open class Member {
    var name: String = ""
    var role: String = ""
    var age: Int = 0
    var joinedDate: Date = Date()

    private val memberJoinDateFormat = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
    val joinDate: String
        get() = memberJoinDateFormat.format(joinedDate)

    constructor(name: String, role: String, age: Int, joinedDate: Date) {
        this.name = name
        this.role = role
        this.age = age
        this.joinedDate = joinedDate
    }

    constructor()
}
