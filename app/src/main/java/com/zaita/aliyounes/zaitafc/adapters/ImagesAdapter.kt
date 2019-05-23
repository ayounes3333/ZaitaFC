package com.zaita.aliyounes.zaitafc.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.chat.model.Media
import com.zaita.aliyounes.zaitafc.databinding.ItemImageAddLayoutBinding
import com.zaita.aliyounes.zaitafc.databinding.ItemImageSquareLayoutBinding
import durdinapps.rxfirebase2.RxFirebaseRecyclerAdapter


class ImagesAdapter(private val context: Context, isEditing: Boolean) : RxFirebaseRecyclerAdapter<RecyclerView.ViewHolder, Media>(Media::class.java) {
    private var listener: ImageClickListener? = null
    private var isEditing = false

    fun setOnClickListener(listener: ImageClickListener) {
        this.listener = listener
    }

    init {
        this.isEditing = isEditing
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val binding = DataBindingUtil.inflate<ItemImageSquareLayoutBinding>(LayoutInflater.from(context), R.layout.item_image_square_layout, parent, false)
            ImagesViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemImageAddLayoutBinding>(LayoutInflater.from(context), R.layout.item_image_add_layout, parent, false)
            ImageAddViewHolder(binding)
        }
    }

    override fun itemAdded(media: Media, s: String, i: Int) {

    }

    override fun itemChanged(media: Media, t1: Media, s: String, i: Int) {

    }

    override fun itemRemoved(media: Media, s: String, i: Int) {

    }

    override fun itemMoved(media: Media, s: String, i: Int, i1: Int) {

    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].type == Media.MediaTypes.ADD) 2 else 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageAddViewHolder) {
            holder.binding.root.setOnClickListener {
                if (listener != null) {
                    listener!!.onAddClick()
                }
            }
        } else if (holder is ImagesViewHolder) {
            // isplay image from firebase
            if (isEditing) {
                FirebaseStorage.getInstance().reference.child(items[position].path.substring(1)).downloadUrl.addOnSuccessListener { uri ->
                    // display returned image
                    Glide.with(context).load(uri.toString()).into(holder.binding.imageViewImage)
                }.addOnFailureListener { it.printStackTrace() }
            } else {
                //Display local image
                Glide.with(context)
                        .load(items[position].path)
                        .into(holder.binding.imageViewImage)
            }

            holder.binding.root.setOnClickListener {
                if (listener != null) {
                    listener!!.onImageClick(holder.getAdapterPosition())
                }
            }
            holder.binding.imageViewClear.setOnClickListener {
                if (listener != null) {
                    listener!!.onRemoveImageClick(holder.getAdapterPosition())
                }
            }
        }
    }

    interface ImageClickListener {
        fun onImageClick(position: Int)
        fun onAddClick()
        fun onRemoveImageClick(position: Int)
    }

    class ImagesViewHolder(var binding: ItemImageSquareLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    class ImageAddViewHolder(var binding: ItemImageAddLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}