package com.zaita.aliyounes.zaitafc.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView

import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.helpers.PrefUtils

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val img = findViewById<ImageView>(R.id.imageView_splash)
        img.post {
            val handler = Handler()
            handler.postDelayed({
                if (PrefUtils.getBoolean(this@SplashActivity, PrefUtils.Prefs.IS_USER_LOGGED_IN, false)) {
                    val i = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    val i = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(i)
                    finish()
                }
            }, 2000)
        }
    }
}
