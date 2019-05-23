package com.zaita.aliyounes.zaitafc.fragments


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.activities.AddNewsArticleActivity
import com.zaita.aliyounes.zaitafc.adapters.NewsAdapter
import com.zaita.aliyounes.zaitafc.chat.model.Media
import com.zaita.aliyounes.zaitafc.network.MediaNetworkCalls
import com.zaita.aliyounes.zaitafc.network.NewsNetworkCalls
import com.zaita.aliyounes.zaitafc.pojos.Post
import com.zaita.aliyounes.zaitafc.pojos.PostType

import java.io.IOException

import cn.jzvd.JZMediaManager
import cn.jzvd.Jzvd
import cn.jzvd.JzvdMgr
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import io.reactivex.disposables.CompositeDisposable


/**
 * A simple [Fragment] subclass.
 */
class NewsFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var floatingActionButtonAdd: FloatingActionButton? = null
    private var adapter: NewsAdapter? = null
    private val compositeDisposable = CompositeDisposable()

    private fun setupRecyclerView(rootView: View) {
        recyclerView = rootView.findViewById(R.id.recyclerView_newsFeed)
        adapter = NewsAdapter(context!!, false)
        recyclerView!!.adapter = adapter
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {

            }

            override fun onChildViewDetachedFromWindow(view: View) {
                val jzvd = view.findViewById<Jzvd>(R.id.videoPlayer)
                if (jzvd?.jzDataSource != null && jzvd.jzDataSource.containsTheUrl(JZMediaManager.getCurrentUrl())) {
                    val currentJzvd = JzvdMgr.getCurrentJzvd()
                    if (currentJzvd != null && currentJzvd.currentScreen != Jzvd.SCREEN_WINDOW_FULLSCREEN) {
                        Jzvd.releaseAllVideos()
                    }
                }
            }
        })
    }

    private fun populateData() {
        compositeDisposable.add(NewsNetworkCalls.newsPosts.subscribe({ data ->
            //Success
            val post = data.value.getValue(Post::class.java)
            if (post != null) {
                when {
                    post.type == PostType.TEXT_ONLY -> adapter!!.manageChildItem(data)
                    post.type == PostType.PHOTOS -> {
                        val uploaded = intArrayOf(0)
                        compositeDisposable.add(MediaNetworkCalls.getNwsMedia(data.key).subscribe({ mediaData ->
                            if (mediaData.eventType == RxFirebaseChildEvent.EventType.ADDED) {
                                post.mediaURLs.add(mediaData.value.getValue<Media>(Media::class.java)!!.path)
                                uploaded[0]++
                                if (uploaded[0] == post.mediaCount) {
                                    val insertedPosition: Int
                                    val previousIndex = adapter!!.keys.indexOf(mediaData.previousChildName)
                                    val nextIndex = previousIndex + 1
                                    if (nextIndex == adapter!!.items.size) {
                                        adapter!!.items.add(post)
                                        adapter!!.keys.add(mediaData.key)
                                    } else {
                                        adapter!!.items.add(nextIndex, post)
                                        adapter!!.keys.add(nextIndex, mediaData.key)
                                    }

                                    insertedPosition = nextIndex

                                    adapter!!.notifyItemInserted(insertedPosition)
                                }
                            } else {
                                adapter!!.manageChildItem(mediaData)
                            }
                        }, { error ->
                            error.printStackTrace()
                            if (error is IOException) {
                                // this is most likely a connection error, so we need to show a message toast.
                                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
                            } else {
                                // we need to show the exception details in a message toast.
                                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                                error.printStackTrace()
                            }
                        }))
                    }
                    else -> compositeDisposable.add(MediaNetworkCalls.getNwsMedia(data.key).subscribe({ mediaData ->
                        //Success
                        post.mediaURL = mediaData.value.getValue<Media>(Media::class.java)!!.path
                        val insertedPosition: Int
                        val previousIndex = adapter!!.keys.indexOf(mediaData.previousChildName)
                        val nextIndex = previousIndex + 1
                        if (nextIndex == adapter!!.items.size) {
                            adapter!!.items.add(post)
                            adapter!!.keys.add(mediaData.key)
                        } else {
                            adapter!!.items.add(nextIndex, post)
                            adapter!!.keys.add(nextIndex, mediaData.key)
                        }

                        insertedPosition = nextIndex

                        adapter!!.notifyItemInserted(insertedPosition)
                    }, { error ->
                        error.printStackTrace()
                        if (error is IOException) {
                            // this is most likely a connection error, so we need to show a message toast.
                            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
                        } else {
                            // we need to show the exception details in a message toast.
                            Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                            error.printStackTrace()
                        }
                    }))
                }
            }
        }, { throwable ->
            //Error
            // print exception stack
            throwable.printStackTrace()
            if (throwable is IOException) {
                // this is most likely a connection error, so we need to show a message toast.
                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
            } else {
                // we need to show the exception details in a message toast.
                Toast.makeText(context, throwable.message, Toast.LENGTH_SHORT).show()
                throwable.printStackTrace()
            }
        }))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        floatingActionButtonAdd = view.findViewById(R.id.FAB_add)
        floatingActionButtonAdd!!.setOnClickListener { startActivity(Intent(context, AddNewsArticleActivity::class.java)) }
        setupRecyclerView(view)
        //populateData();
    }

    override fun onResume() {
        super.onResume()
        if (adapter != null) {
            adapter!!.items.clear()
            adapter!!.keys.clear()
            adapter!!.dispose()
            adapter!!.notifyDataSetChanged()
        }
        populateData()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        adapter!!.dispose()
        super.onDestroy()
    }

    companion object {
        val TAG: String = NewsFragment::class.java.simpleName
    }
}// Required empty public constructor
