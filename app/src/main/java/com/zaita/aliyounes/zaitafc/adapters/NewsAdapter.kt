package com.zaita.aliyounes.zaitafc.adapters

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.helpers.ImageUtils
import com.zaita.aliyounes.zaitafc.pojos.Post
import com.zaita.aliyounes.zaitafc.pojos.PostType
import durdinapps.rxfirebase2.RxFirebaseRecyclerAdapter
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.relex.circleindicator.CircleIndicator

/**
 * Created by Ali Younes on 8/11/2017.
 */

class NewsAdapter(private val context: Context, @Suppress("unused") private val isAdmin: Boolean) : RxFirebaseRecyclerAdapter<NewsAdapter.PostVieHolder, Post>(Post::class.java) {

    //List of getItems() to show
    private val compositeDisposable = CompositeDisposable()


    override fun getItemViewType(position: Int): Int {
        val post = items[position]
        when (post.type) {
            PostType.TEXT_ONLY -> return 1
            PostType.SINGLE_IMAGE -> return 2
            PostType.PHOTOS -> return 3
            PostType.VIDEO -> return 4
        }
        return -1
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PostVieHolder {
        val view: View
        when (i) {
            1 -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_news_text_layout, viewGroup, false)
                return TextPostViewHolder(view)
            }
            2 -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_news_photo_layout, viewGroup, false)
                return ImagePostViewHolder(view)
            }
            3 -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_news_images_layout, viewGroup, false)
                return ImagesPostViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_news_video_layout, viewGroup, false)
                return VideoPostViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(postViewHolder: PostVieHolder, position: Int) {
        val post = items[position]
        postViewHolder.bind(post)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun itemAdded(post: Post, s: String, i: Int) {

    }

    override fun itemChanged(post: Post, t1: Post, s: String, i: Int) {

    }

    override fun itemRemoved(post: Post, s: String, i: Int) {

    }

    override fun itemMoved(post: Post, s: String, i: Int, i1: Int) {

    }


    abstract inner class PostVieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewPostTitle: TextView = itemView.findViewById(R.id.textView_postTitle)
        private val textViewPostBody: TextView = itemView.findViewById(R.id.textView_postBody)
        private val textViewPostDate: TextView = itemView.findViewById(R.id.textView_postDate)
        private val cardViewPost: CardView = itemView.findViewById(R.id.cardView_post)

        //Bind data to List item
        internal open fun bind(post: Post) {
            textViewPostTitle.text = post.title
            textViewPostBody.text = post.body
            textViewPostDate.text = post.creationDate
            cardViewPost.setOnClickListener {
                //Add action for any post item click
            }
        }
    }

    private inner class ImagePostViewHolder internal constructor(itemView: View) : PostVieHolder(itemView) {

        private val imageViewPostImage: ImageView = itemView.findViewById(R.id.imageView_newsPhoto)

        override fun bind(post: Post) {
            super.bind(post)
            FirebaseStorage.getInstance().reference.child(post.mediaURL.substring(1)).downloadUrl.addOnSuccessListener { uri ->
                // display returned image
                Glide.with(context).load(uri.toString()).into(imageViewPostImage)
            }.addOnFailureListener { it.printStackTrace() }
        }
    }

    private inner class ImagesPostViewHolder internal constructor(itemView: View) : PostVieHolder(itemView) {
        private val viewPager: ViewPager = itemView.findViewById(R.id.pager)
        private val indicator: CircleIndicator = itemView.findViewById(R.id.indicator)

        override fun bind(post: Post) {
            super.bind(post)
            viewPager.adapter = ImagePagerAdapter(itemView.context, post.mediaURLs)
            indicator.setViewPager(viewPager)
        }
    }

    private inner class VideoPostViewHolder internal constructor(itemView: View) : PostVieHolder(itemView) {

        private var videoView: JzvdStd? = null


        override fun bind(post: Post) {
            super.bind(post)
            videoView = itemView.findViewById(R.id.videoPlayer)
            FirebaseStorage.getInstance().reference.child(post.mediaURL.substring(1)).downloadUrl
                    .addOnSuccessListener { uri ->
                        videoView!!.setUp(uri.toString(), "", Jzvd.SCREEN_WINDOW_LIST)
                        videoView!!.fullscreenButton.visibility = View.GONE
                        ImageUtils.getVideoThumbnailRx(uri).subscribe(object : Observer<ByteArray> {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onNext(stream: ByteArray) {
                                Glide.with(context)
                                        .load(stream)
                                        .into(videoView!!.thumbImageView)
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                            }

                            override fun onComplete() {

                            }
                        })
                    }.addOnFailureListener { it.printStackTrace() }
        }
    }

    fun dispose() {
        compositeDisposable.dispose()
    }

    private inner class TextPostViewHolder internal constructor(itemView: View) : PostVieHolder(itemView)
}
