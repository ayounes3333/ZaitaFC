package com.zaita.aliyounes.zaitafc.pojos

import android.os.Parcel
import android.os.Parcelable

/*
 used to hold user data when registering (in the registration activity
 */
class UserRegistrationData : Parcelable {

    var password: String = ""
    @Suppress("MemberVisibilityCanBePrivate")
    var isAdmin: Boolean = false
    var age: Int = 0
    var birthday: String = ""
    var email: String = ""
    var name: String = ""
    var position: String = ""
    var genderId: Int = 0

    constructor()

    private constructor(`in`: Parcel) {
        password = `in`.readString()!!
        isAdmin = `in`.readByte().toInt() != 0
        age = `in`.readInt()
        birthday = `in`.readString()!!
        email = `in`.readString()!!
        name = `in`.readString()!!
        position = `in`.readString()!!
        genderId = `in`.readInt()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(password)
        dest.writeByte((if (isAdmin) 1 else 0).toByte())
        dest.writeInt(age)
        dest.writeString(birthday)
        dest.writeString(email)
        dest.writeString(name)
        dest.writeString(position)
        dest.writeInt(genderId)
    }

    fun asUserDetails(): UserDetails {
        val userDetails = UserDetails()
        userDetails.email = email
        userDetails.age = age
        userDetails.genderId = genderId
        userDetails.name = name
        userDetails.position = position
        userDetails.setBirthday(birthday)
        userDetails.isAdmin = isAdmin
        return userDetails
    }


    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserRegistrationData> {
        override fun createFromParcel(parcel: Parcel): UserRegistrationData {
            return UserRegistrationData(parcel)
        }

        override fun newArray(size: Int): Array<UserRegistrationData?> {
            return arrayOfNulls(size)
        }
    }
}
