package com.zaita.aliyounes.zaitafc.application

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log

import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.zaita.aliyounes.zaitafc.helpers.PrefUtils

import okhttp3.OkHttpClient

/**
 * Created by Lenovo on 8/18/2017.
 */

class ZaitaFCApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        Stetho.initializeWithDefaults(this)
        OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .build()
        //setDummyData();
        clearDummyData()
        Log.i("Setup", "Complete")
    }

    fun checkIfHasNetwork(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    companion object {
        var instance: ZaitaFCApplication? = null
            private set

        fun hasNetwork(): Boolean {
            return instance!!.checkIfHasNetwork()
        }

        val isDummyData: Boolean
            get() = PrefUtils.getBoolean(ZaitaFCApplication.instance, "isDummyData", false)

        fun setDummyData() {
            PrefUtils.setBoolean(ZaitaFCApplication.instance, "isDummyData", true)
        }

        fun clearDummyData() {
            PrefUtils.setBoolean(ZaitaFCApplication.instance, "isDummyData", false)
        }
    }
}
