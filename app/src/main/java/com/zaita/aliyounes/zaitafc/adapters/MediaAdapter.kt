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
import com.zaita.aliyounes.zaitafc.databinding.ItemImageSquareLayoutBinding
import com.zaita.aliyounes.zaitafc.databinding.ItemMediaAddLayoutBinding
import com.zaita.aliyounes.zaitafc.databinding.ItemVideoSquareLayoutBinding
import com.zaita.aliyounes.zaitafc.helpers.ImageUtils
import durdinapps.rxfirebase2.RxFirebaseRecyclerAdapter
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class MediaAdapter(private val context: Context, isEditing: Boolean) : RxFirebaseRecyclerAdapter<RecyclerView.ViewHolder, Media>(Media::class.java) {
    private var listener: BestShotClickListener? = null
    private val compositeDisposable = CompositeDisposable()
    private var isEditing = false

    fun setOnClickListener(listener: BestShotClickListener) {
        this.listener = listener
    }

    init {
        this.isEditing = isEditing
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                val binding = DataBindingUtil.inflate<ItemVideoSquareLayoutBinding>(LayoutInflater.from(context), R.layout.item_video_square_layout, parent, false)
                VideosViewHolder(binding)
            }
            2 -> {
                val binding = DataBindingUtil.inflate<ItemImageSquareLayoutBinding>(LayoutInflater.from(context), R.layout.item_image_square_layout, parent, false)
                ImagesAdapter.ImagesViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<ItemMediaAddLayoutBinding>(LayoutInflater.from(context), R.layout.item_media_add_layout, parent, false)
                MediaAddViewHolder(binding)
            }
        }
    }

    fun dispose() {
        compositeDisposable.dispose()
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
        return when {
            items[position].type == Media.MediaTypes.ADD -> 3
            items[position].type == Media.MediaTypes.IMAGE -> 2
            items[position].type == Media.MediaTypes.VIDEO -> 1
            else -> 0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MediaAddViewHolder -> holder.binding.root.setOnClickListener {
                if (listener != null) {
                    listener!!.onAddClick()
                }
            }
            is VideosViewHolder -> {
                if (isEditing) {
                    FirebaseStorage.getInstance().reference.child(items[position].path.substring(1)).downloadUrl.addOnSuccessListener { uri ->
                        ImageUtils.getVideoThumbnailRx(uri).subscribe(object : Observer<ByteArray> {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onNext(stream: ByteArray) {
                                Glide.with(context)
                                        .load(stream)
                                        .into(holder.binding.imageViewVideo)
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }

                            override fun onComplete() {

                            }
                        })
                    }.addOnFailureListener { exception -> exception.printStackTrace() }
                } else {
                    Glide.with(context)
                            .load(items[position].path)
                            .into(holder.binding.imageViewVideo)
                }

                holder.binding.root.setOnClickListener {
                    if (listener != null) {
                        listener!!.onBestShotClick(holder.getAdapterPosition())
                    }
                }
                holder.binding.imageViewClear.setOnClickListener {
                    if (listener != null) {
                        listener!!.onRemoveBestShotClick(holder.getAdapterPosition())
                    }
                }
            }
            is ImagesAdapter.ImagesViewHolder -> {
                if (isEditing) {
                    FirebaseStorage.getInstance().reference.child(items[position].path.substring(1)).downloadUrl.addOnSuccessListener { uri ->
                        Glide.with(context)
                                .load(uri.toString())
                                .into(holder.binding.imageViewImage)
                    }.addOnFailureListener { exception -> exception.printStackTrace() }
                } else {
                    Glide.with(context)
                            .load(items[position].path)
                            .into(holder.binding.imageViewImage)
                }

                holder.binding.root.setOnClickListener {
                    if (listener != null) {
                        listener!!.onBestShotClick(holder.getAdapterPosition())
                    }
                }
                holder.binding.imageViewClear.setOnClickListener {
                    if (listener != null) {
                        listener!!.onRemoveBestShotClick(holder.getAdapterPosition())
                    }
                }
            }
        }
    }

    interface BestShotClickListener {
        fun onBestShotClick(position: Int)
        fun onAddClick()
        fun onRemoveBestShotClick(position: Int)
    }
}