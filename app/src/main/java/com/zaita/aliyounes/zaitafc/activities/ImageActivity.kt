package com.zaita.aliyounes.zaitafc.activities

import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.storage.FirebaseStorage
import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {

    private var binding: ActivityImageBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_image)
        val path = intent.getStringExtra("path")
        FirebaseStorage.getInstance().reference.child(path.substring(1)).downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this@ImageActivity)
                    .load(uri)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            e?.printStackTrace()
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            binding!!.progressbarLoading.visibility = View.GONE
                            return false
                        }
                    })
                    .into(binding!!.photoView)
        }
    }
}
