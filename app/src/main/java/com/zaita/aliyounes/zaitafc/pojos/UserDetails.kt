@file:Suppress("unused")

package com.zaita.aliyounes.zaitafc.pojos

import android.os.Parcel
import android.os.Parcelable

import com.zaita.aliyounes.zaitafc.helpers.DateTimeUtils

import java.util.Date

/**
 * Created by Lenovo on 12/3/2017.
 */

class UserDetails : Parcelable {
    var age: Int = 0
    private var birthday: String? = null
    var genderId: Int = 0
    var email: String = ""
    var name: String = ""
    var position: String = ""
    var isAdmin: Boolean = false

    constructor()
    constructor(email: String, name: String) {
        this.email = email
        this.name = name
    }


    private constructor(`in`: Parcel) {
        age = `in`.readInt()
        birthday = `in`.readString()
        genderId = `in`.readInt()
        email = `in`.readString()!!
        name = `in`.readString()!!
        position = `in`.readString()!!
        isAdmin = `in`.readByte().toInt() != 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(age)
        dest.writeString(birthday)
        dest.writeInt(genderId)
        dest.writeString(email)
        dest.writeString(name)
        dest.writeString(position)
        dest.writeByte((if (isAdmin) 1 else 0).toByte())
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getBirthday(): String? {
        return birthday
    }

    fun setBirthday(birthday: String) {
        this.birthday = birthday
        this.age = DateTimeUtils.getDiffYears(Date(), DateTimeUtils.fromDayDateString(this.birthday as String)!!)
    }

    companion object CREATOR : Parcelable.Creator<UserDetails> {
        override fun createFromParcel(parcel: Parcel): UserDetails {
            return UserDetails(parcel)
        }

        override fun newArray(size: Int): Array<UserDetails?> {
            return arrayOfNulls(size)
        }
    }
}
