@file:Suppress("unused")

package com.zaita.aliyounes.zaitafc.helpers

import android.content.Context
import android.preference.PreferenceManager
import com.zaita.aliyounes.zaitafc.application.ZaitaFCApplication
import java.util.*

/*
    this is a utility class used to store different data types and some key data like username in the shared preferences of the android app.
    See: https://developer.android.com/training/data-storage/shared-preferences
 */

class PrefUtils {
    // , Context.MODE_PRIVATE

    object Session {
        var userEmail: String?
            get() = getString(ZaitaFCApplication.instance, "UserEmail", "")
            set(userEmail) {
                setString(ZaitaFCApplication.instance, "UserEmail", userEmail?:"")
            }
        var userId: String?
            get() = getString(ZaitaFCApplication.instance, "UserId", "")
            set(userId) {
                setString(ZaitaFCApplication.instance, "UserId", userId?:"")
            }
        var isAdmin: Boolean
            get() = getBoolean(ZaitaFCApplication.instance, "isAdmin", false)
            set(isAdmin) {
                setBoolean(ZaitaFCApplication.instance, "isAdmin", isAdmin)
            }
        var userName: String?
            get() = getString(ZaitaFCApplication.instance, "UserName", "")
            set(userName) {
                setString(ZaitaFCApplication.instance, "UserName", userName?:"")
            }
        var position: String?
            get() = getString(ZaitaFCApplication.instance, "Position", "")
            set(position) {
                setString(ZaitaFCApplication.instance, "Position", position?:"")
            }
        var age: Int
            get() = getInt(ZaitaFCApplication.instance!!, "Age", 0)
            set(age) {
                setInt(ZaitaFCApplication.instance, "Age", age)
            }
        var genderId: Int
            get() = getInt(ZaitaFCApplication.instance!!, "GenderId", 0)
            set(genderId) {
                setInt(ZaitaFCApplication.instance, "GenderId", genderId)
            }
    }

    object Prefs {
        const val IS_USER_LOGGED_IN = "IsUserLoggedIn"
    }

    companion object {

        fun getInt(context: Context, key: Int, defValue: Int): Int {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context.applicationContext)
            return pref.getInt(context.getString(key), defValue)
        }

        fun getInt(context: Context, key: String, defValue: Int): Int {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context.applicationContext)
            return pref.getInt(key, defValue)
        }

        fun getString(context: Context, key: Int, defValue: String): String? {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context)
            return pref.getString(context.getString(key), defValue)
        }

        fun getString(context: Context?, key: String, defValue: String): String? {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context)
            return pref.getString(key, defValue)
        }

        fun getBoolean(context: Context, key: Int, defValue: Boolean): Boolean {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context)
            return pref.getBoolean(context.getString(key), defValue)
        }

        fun getBoolean(context: Context?, key: String, defValue: Boolean): Boolean {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context)
            return pref.getBoolean(key, defValue)
        }

        fun setString(context: Context?, key: String, value: String): Boolean {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context)
            val editor = pref.edit()
            editor.putString(key, value)
            return editor.commit()
        }

        fun setBoolean(context: Context?, key: String, value: Boolean): Boolean {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context)
            val editor = pref.edit()
            editor.putBoolean(key, value)
            return editor.commit()
        }

        fun setBoolean(context: Context, key: Int, value: Boolean): Boolean {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context)
            val editor = pref.edit()
            editor.putBoolean(context.getString(key), value)
            return editor.commit()
        }

        fun setInt(context: Context, key: Int, value: Int): Boolean {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context)
            val editor = pref.edit()
            editor.putInt(context.getString(key), value)
            return editor.commit()
        }

        fun setInt(context: Context?, key: String, value: Int): Boolean {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context)
            val editor = pref.edit()
            editor.putInt(key, value)
            return editor.commit()
        }

        fun setStringSet(context: Context, key: String, value: Set<String>): Boolean {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context)
            val editor = pref.edit()
            editor.putStringSet(key, value)
            return editor.commit()
        }

        fun getStringSet(context: Context, key: String): Set<String> {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context)
            return pref.getStringSet(key, HashSet())!!
        }

        fun clearAll(context: Context) {
            val pref = PreferenceManager
                    .getDefaultSharedPreferences(context)
            val editor = pref.edit()
            editor.clear().apply()
        }

        fun clearSpecificPref(context: Context, key: String) {
            val settings = PreferenceManager
                    .getDefaultSharedPreferences(context)
            settings.edit().remove(key).apply()
        }
    }
}
