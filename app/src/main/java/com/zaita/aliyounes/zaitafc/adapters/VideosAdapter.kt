@file:Suppress("unused")

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
import com.zaita.aliyounes.zaitafc.databinding.ItemVideoAddLayoutBinding
import com.zaita.aliyounes.zaitafc.databinding.ItemVideoSquareLayoutBinding
import com.zaita.aliyounes.zaitafc.helpers.ImageUtils
import durdinapps.rxfirebase2.RxFirebaseRecyclerAdapter
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by lenovo.
 */
class VideosAdapter(private val context: Context, isEditing: Boolean) : RxFirebaseRecyclerAdapter<RecyclerView.ViewHolder, Media>(Media::class.java) {
    private var listener: VideoClickListener? = null
    private var isEditing = false
    private val compositeDisposable = CompositeDisposable()

    fun setOnClickListener(listener: VideoClickListener) {
        this.listener = listener
    }

    init {
        this.isEditing = isEditing
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val binding = DataBindingUtil.inflate<ItemVideoSquareLayoutBinding>(LayoutInflater.from(context), R.layout.item_video_square_layout, parent, false)
            VideosViewHolder(binding)
        } else {
            val binding = DataBindingUtil.inflate<ItemVideoAddLayoutBinding>(LayoutInflater.from(context), R.layout.item_video_add_layout, parent, false)
            VideoAddViewHolder(binding)
        }
    }

    override fun getItemCount(): Int {
        return items.size
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

    @Suppress("unused")
    fun dispose() {
        compositeDisposable.dispose()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is VideoAddViewHolder) {
            holder.binding.root.setOnClickListener {
                if (listener != null) {
                    listener!!.onAddClick()
                }
            }
        } else if (holder is VideosViewHolder) {

            if (isEditing) {
                // load video from firebase

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
                }.addOnFailureListener { it.printStackTrace() }
            } else {
                // load local video
                Glide.with(context)
                        .load(items[position].path)
                        .into(holder.binding.imageViewVideo)
            }

            holder.binding.root.setOnClickListener {
                if (listener != null) {
                    listener!!.onVideoClick(holder.getAdapterPosition())
                }
            }
            holder.binding.imageViewClear.setOnClickListener {
                if (listener != null) {
                    listener!!.onRemoveVideoClick(holder.getAdapterPosition())
                }
            }
        }
    }

    interface VideoClickListener {
        fun onVideoClick(position: Int)
        fun onAddClick()
        fun onRemoveVideoClick(position: Int)
    }
}