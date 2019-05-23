package com.zaita.aliyounes.zaitafc.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.zaita.aliyounes.zaitafc.R

class ImagePagerAdapter internal constructor(context: Context, private val images: List<String>) : PagerAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val myImageLayout = inflater.inflate(R.layout.slider_image, view, false)
        val myImage = myImageLayout
                .findViewById<ImageView>(R.id.imageView_photo)
        FirebaseStorage.getInstance().reference.child(images[position].substring(1)).downloadUrl.addOnSuccessListener { uri ->
            // display returned image
            Glide.with(myImage.context).load(uri.toString()).into(myImage)
        }.addOnFailureListener { it.printStackTrace() }
        view.addView(myImageLayout, 0)
        return myImageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
}
