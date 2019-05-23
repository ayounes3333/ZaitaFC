package com.zaita.aliyounes.zaitafc.pojos

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by Lenovo on 10/21/2017.
 */

class Player : Member {
    var number: Int = 0
    var gameId = 1

    constructor(name: String, number: Int, role: String, age: Int, joinedDate: Date) {
        this.number = number
        this.name = name
        this.role = role
        this.age = age
        this.joinedDate = joinedDate
    }

    constructor() {

    }
}
