package com.zaita.aliyounes.zaitafc.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.erikagtierrez.multiple_media_picker.Gallery
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.adapters.MediaAdapter
import com.zaita.aliyounes.zaitafc.chat.model.Media
import com.zaita.aliyounes.zaitafc.databinding.ActivityAddNewsArticleBinding
import com.zaita.aliyounes.zaitafc.helpers.ImageUtils
import com.zaita.aliyounes.zaitafc.helpers.TextUtils
import com.zaita.aliyounes.zaitafc.network.MediaNetworkCalls
import com.zaita.aliyounes.zaitafc.network.NewsNetworkCalls
import com.zaita.aliyounes.zaitafc.pojos.Post
import com.zaita.aliyounes.zaitafc.pojos.PostType
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.IOException
import java.util.*

class AddNewsArticleActivity : AppCompatActivity() {

    private var binding: ActivityAddNewsArticleBinding? = null
    private val post = Post()
    private var postId = ""
    private val media = ArrayList<Media>()
    private var isMediaSelected = false
    private var adapter: MediaAdapter? = null
    private val compositeDisposable = CompositeDisposable()

    private val isEditing: Boolean
        get() = if (intent != null) {
            intent.getBooleanExtra("isEditing", false)
        } else false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent != null) {
            postId = intent.getStringExtra("postId")
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_news_article)
        setupViews()
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupViews() {
        binding!!.radioButtonImage.setOnCheckedChangeListener { _, b ->
            if (b) {
                post.type = PostType.SINGLE_IMAGE
                binding!!.cardViewAddMedia.visibility = View.VISIBLE
                binding!!.recyclerViewImages.visibility = View.GONE
            }
        }
        binding!!.radioButtonImages.setOnCheckedChangeListener { _, b ->
            if (b) {
                post.type = PostType.PHOTOS
                binding!!.cardViewAddMedia.visibility = View.GONE
                binding!!.recyclerViewImages.visibility = View.VISIBLE
            }
        }
        binding!!.radioButtonText.setOnCheckedChangeListener { _, b ->
            if (b) {
                post.type = PostType.TEXT_ONLY
                binding!!.cardViewAddMedia.visibility = View.GONE
                binding!!.recyclerViewImages.visibility = View.GONE
            }
        }
        binding!!.radioButtonVideo.setOnCheckedChangeListener { _, b ->
            if (b) {
                post.type = PostType.VIDEO
                binding!!.cardViewAddMedia.visibility = View.VISIBLE
                binding!!.recyclerViewImages.visibility = View.GONE
            }
        }
        binding!!.radioButtonText.isChecked = true
        binding!!.cardViewAddMedia.setOnClickListener {
            // We need to check for READ_EXTERNAL_STORAGE permission on runtime to support android marshmallow and higher

            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    // we don't need that here.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_READ_STORAGE_PERMISSION)

                    // REQUEST_READ_STORAGE_PERMISSION is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                pickMedia()
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding!!.toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupRecyclerView() {
        adapter = MediaAdapter(this, false)
        adapter!!.setOnClickListener(object : MediaAdapter.BestShotClickListener {

            override fun onBestShotClick(position: Int) {
                val intent = Intent(this@AddNewsArticleActivity, ImageActivity::class.java)
                intent.putExtra("path", adapter!!.getItem(position).path)
                startActivity(intent)
            }

            override fun onAddClick() {
                // We need to check for READ_EXTERNAL_STORAGE permission on runtime to support android marshmallow and higher

                if (ContextCompat.checkSelfPermission(this@AddNewsArticleActivity,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this@AddNewsArticleActivity,
                                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        // we don't need that here.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(this@AddNewsArticleActivity,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                REQUEST_READ_STORAGE_PERMISSION)

                        // REQUEST_READ_STORAGE_PERMISSION_FOR_IMAGES is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    pickImages()
                }
            }

            override fun onRemoveBestShotClick(position: Int) {
                if (isEditing) {

                    MediaNetworkCalls.deleteImage(postId, adapter!!.keys[position]).subscribe(object : CompletableObserver {
                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }

                        override fun onComplete() {
                            Toast.makeText(this@AddNewsArticleActivity, "Media Removed", Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(e: Throwable) {
                            // print exception stack
                            e.printStackTrace()
                            if (e is IOException) {
                                // this is most likely a connection error, so we need to show a message toast.
                                Toast.makeText(this@AddNewsArticleActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                            } else {
                                // we need to show the exception details in a message toast.
                                Toast.makeText(this@AddNewsArticleActivity, e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                } else {
                    adapter!!.items.removeAt(position)
                    adapter!!.notifyItemRemoved(position)
                }
            }
        })
        binding!!.recyclerViewImages.adapter = adapter
        binding!!.recyclerViewImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        adapter!!.items.add(Media(Media.MediaTypes.ADD))
        adapter!!.notifyItemInserted(0)
    }

    // Open picker for images and videos
    private fun pickMedia() {
        val mode: Int
        val maxSelection: Int
        when (post.type) {
            PostType.TEXT_ONLY -> return
            PostType.SINGLE_IMAGE -> {
                mode = 2
                maxSelection = 1
            }
            PostType.PHOTOS -> {
                mode = 2
                maxSelection = MAX_SELECTION
            }
            PostType.VIDEO -> {
                mode = 3
                maxSelection = 1
            }
            else -> return
        }
        val intent = Intent(this, Gallery::class.java)
        // Set the title
        intent.putExtra("title", "Select media")
        // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
        intent.putExtra("mode", mode)
        intent.putExtra("maxSelection", maxSelection) // Optional
        startActivityForResult(intent, PICK_SINGLE_MEDIA)
    }

    // Open picker for images and videos
    private fun pickImages() {
        val intent = Intent(this, Gallery::class.java)
        // Set the title
        intent.putExtra("title", "Select media")
        // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
        intent.putExtra("mode", 2)
        intent.putExtra("maxSelection", MAX_SELECTION) // Optional
        startActivityForResult(intent, PICK_IMAGES)
    }

    private fun uploadMedia() {
        val mediaCount = media.size
        val uploaded = intArrayOf(0)
        showUploadProgress()
        MediaNetworkCalls.uploadNewsMedia(media).subscribe(object : Observer<Pair<Media, Int>> {
            override fun onSubscribe(d: Disposable) {
                compositeDisposable.add(d)
            }

            override fun onNext(mediaIntegerPair: Pair<Media, Int>) {
                uploaded[0]++
                updateProgress(uploaded[0], mediaCount)
                MediaNetworkCalls.addNewsMediaToDB(postId, mediaIntegerPair.first).subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onComplete() {
                        Log.d("AddNewsMediaToDB", "Added: " + mediaIntegerPair.first.path)
                    }

                    override fun onError(e: Throwable) {
                        // print exception stack
                        e.printStackTrace()
                        if (e is IOException) {
                            // this is most likely a connection error, so we need to show a message toast.
                            Toast.makeText(this@AddNewsArticleActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                        } else {
                            // we need to show the exception details in a message toast.
                            Toast.makeText(this@AddNewsArticleActivity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
                if (uploaded[0] == mediaCount) {
                    finish()
                }
            }

            override fun onError(e: Throwable) {
                // print exception stack
                e.printStackTrace()
                if (e is IOException) {
                    // this is most likely a connection error, so we need to show a message toast.
                    Toast.makeText(this@AddNewsArticleActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                } else {
                    // we need to show the exception details in a message toast.
                    Toast.makeText(this@AddNewsArticleActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onComplete() {

            }
        })
    }

    private fun checkFields(): Boolean {
        if (binding!!.editTextTitle.text.toString().isEmpty()) {
            binding!!.textInputTitle.error = getString(R.string.error_empty_title)
            return false
        } else {
            post.title = binding!!.editTextTitle.text.toString()
        }
        if (binding!!.editTextBody.text.toString().isEmpty()) {
            binding!!.editTextBody.error = getString(R.string.error_empty_body)
            return false
        } else {
            post.body = binding!!.editTextBody.text.toString()
        }
        if (post.type == 0) {
            Toast.makeText(this@AddNewsArticleActivity, getString(R.string.error_empty_post_type), Toast.LENGTH_SHORT).show()
            return false
        }
        if (post.type != PostType.TEXT_ONLY && !isMediaSelected) {
            Toast.makeText(this@AddNewsArticleActivity, getString(R.string.error_empty_media), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    // manage toolbar menu selection (n this case logout i pressed)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save) {
            if (checkFields())
                NewsNetworkCalls.addNewsPost(post).subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onNext(createdPostId: String) {
                        postId = createdPostId
                        if (post.type != PostType.TEXT_ONLY)
                            uploadMedia()
                        else
                            finish()
                    }

                    override fun onError(e: Throwable) {
                        // print exception stack
                        e.printStackTrace()
                        if (e is IOException) {
                            // this is most likely a connection error, so we need to show a message toast.
                            Toast.makeText(this@AddNewsArticleActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                        } else {
                            // we need to show the exception details in a message toast.
                            Toast.makeText(this@AddNewsArticleActivity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onComplete() {
                        Log.d("Add Post", "Complete")
                    }
                })
        } else if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_STORAGE_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // storage-related task you need to do.
                    pickMedia()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(
                            this,
                            R.string.error_permission_denied,
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }// other 'case' lines to check for other
        // permissions this app might request.
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        when (requestCode) {
            PICK_SINGLE_MEDIA -> {
                // Make sure the request was successful
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectionResult = data.getStringArrayListExtra("result")
                    val selectedMedia = ArrayList<Media>()
                    for (path in selectionResult) {
                        when {
                            TextUtils.isImagePath(path) -> selectedMedia.add(Media(Uri.fromFile(File(path)).toString(), FirebaseAuth.getInstance().uid!!, Media.MediaTypes.IMAGE))
                            TextUtils.isVideoPath(path) -> selectedMedia.add(Media(Uri.fromFile(File(path)).toString(), FirebaseAuth.getInstance().uid!!, Media.MediaTypes.VIDEO))
                            else -> Toast.makeText(
                                    this,
                                    R.string.error_unsupported_format,
                                    Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
                    post.mediaCount = 1
                    isMediaSelected = true
                    if (isEditing) {
                        showUploadProgress()
                        MediaNetworkCalls.uploadNewsMedia(selectedMedia).subscribe(object : Observer<Pair<Media, Int>> {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onNext(mediaIntegerPair: Pair<Media, Int>) {
                                updateProgress(mediaIntegerPair.second, selectedMedia.size)

                                MediaNetworkCalls.addNewsMediaToDB(postId, mediaIntegerPair.first).subscribe(object : CompletableObserver {
                                    override fun onSubscribe(d: Disposable) {
                                        compositeDisposable.add(d)
                                    }

                                    override fun onComplete() {
                                        Log.d("AddVideoToDB", "Added: " + mediaIntegerPair.first.path)
                                    }

                                    override fun onError(e: Throwable) {
                                        // print exception stack
                                        e.printStackTrace()
                                        if (e is IOException) {
                                            // this is most likely a connection error, so we need to show a message toast.
                                            Toast.makeText(this@AddNewsArticleActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                                        } else {
                                            // we need to show the exception details in a message toast.
                                            Toast.makeText(this@AddNewsArticleActivity, e.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                })
                                if (mediaIntegerPair.second == selectedMedia.size) {
                                    hideUploadProgress()
                                }
                            }

                            override fun onError(e: Throwable) {
                                // print exception stack
                                e.printStackTrace()
                                if (e is IOException) {
                                    // this is most likely a connection error, so we need to show a message toast.
                                    Toast.makeText(this@AddNewsArticleActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                                } else {
                                    // we need to show the exception details in a message toast.
                                    Toast.makeText(this@AddNewsArticleActivity, e.message, Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onComplete() {

                            }
                        })
                    } else {
                        if (selectedMedia[0].type == Media.MediaTypes.VIDEO) {
                            binding!!.imageViewVideo.visibility = View.VISIBLE
                            if (isEditing) {
                                // load video from firebase

                                FirebaseStorage.getInstance().reference.child(selectedMedia[0].path.substring(1)).downloadUrl.addOnSuccessListener { uri ->
                                    ImageUtils.getVideoThumbnailRx(uri).subscribe(object : Observer<ByteArray> {
                                        override fun onSubscribe(d: Disposable) {
                                            compositeDisposable.add(d)
                                        }

                                        override fun onNext(stream: ByteArray) {
                                            Glide.with(this@AddNewsArticleActivity)
                                                    .load(stream)
                                                    .into(binding!!.imageViewMedia)
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
                                Glide.with(this)
                                        .load(selectedMedia[0].path)
                                        .into(binding!!.imageViewMedia)
                                this.media.clear()
                                this.media.add(selectedMedia[0])
                            }
                        } else {
                            binding!!.imageViewVideo.visibility = View.GONE
                            if (isEditing) {
                                FirebaseStorage.getInstance().reference.child(selectedMedia[0].path.substring(1)).downloadUrl.addOnSuccessListener { uri ->
                                    // display returned image
                                    Glide.with(this@AddNewsArticleActivity)
                                            .load(uri.toString())
                                            .into(binding!!.imageViewMedia)
                                }.addOnFailureListener { it.printStackTrace() }
                            } else {
                                //Display local image
                                Glide.with(this)
                                        .load(selectedMedia[0].path)
                                        .into(binding!!.imageViewMedia)
                                this.media.clear()
                                this.media.add(selectedMedia[0])
                            }
                        }
                    }
                }
            }
            PICK_IMAGES -> {
                // Make sure the request was successful
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val selectionResult = data.getStringArrayListExtra("result")
                    val selectedMedia = ArrayList<Media>()
                    for (path in selectionResult) {
                        when {
                            TextUtils.isImagePath(path) -> selectedMedia.add(Media(Uri.fromFile(File(path)).toString(), FirebaseAuth.getInstance().uid!!, Media.MediaTypes.IMAGE))
                            TextUtils.isVideoPath(path) -> selectedMedia.add(Media(Uri.fromFile(File(path)).toString(), FirebaseAuth.getInstance().uid!!, Media.MediaTypes.VIDEO))
                            else -> Toast.makeText(
                                    this,
                                    R.string.error_unsupported_format,
                                    Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
                    post.mediaCount = selectedMedia.size
                    isMediaSelected = true
                    if (isEditing) {
                        showUploadProgress()
                        MediaNetworkCalls.uploadNewsMedia(selectedMedia).subscribe(object : Observer<Pair<Media, Int>> {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onNext(mediaIntegerPair: Pair<Media, Int>) {
                                updateProgress(mediaIntegerPair.second, selectedMedia.size)

                                MediaNetworkCalls.addNewsMediaToDB(postId, mediaIntegerPair.first).subscribe(object : CompletableObserver {
                                    override fun onSubscribe(d: Disposable) {
                                        compositeDisposable.add(d)
                                    }

                                    override fun onComplete() {
                                        Log.d("AddVideoToDB", "Added: " + mediaIntegerPair.first.path)
                                    }

                                    override fun onError(e: Throwable) {
                                        // print exception stack
                                        e.printStackTrace()
                                        if (e is IOException) {
                                            // this is most likely a connection error, so we need to show a message toast.
                                            Toast.makeText(this@AddNewsArticleActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                                        } else {
                                            // we need to show the exception details in a message toast.
                                            Toast.makeText(this@AddNewsArticleActivity, e.message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                })
                                if (mediaIntegerPair.second == selectedMedia.size) {
                                    hideUploadProgress()
                                }
                            }

                            override fun onError(e: Throwable) {
                                // print exception stack
                                e.printStackTrace()
                                if (e is IOException) {
                                    // this is most likely a connection error, so we need to show a message toast.
                                    Toast.makeText(this@AddNewsArticleActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                                } else {
                                    // we need to show the exception details in a message toast.
                                    Toast.makeText(this@AddNewsArticleActivity, e.message, Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onComplete() {

                            }
                        })
                    } else {
                        this.media.clear()
                        this.media.addAll(selectedMedia)
                        adapter!!.items.addAll(selectedMedia)
                        adapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun hideUploadProgress() {
        binding!!.relativeLayoutUpload.root.visibility = View.GONE
    }

    private fun showUploadProgress() {
        binding!!.relativeLayoutUpload.root.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun updateProgress(current: Int, total: Int) {
        binding!!.relativeLayoutUpload.progressBarUpload.max = total
        binding!!.relativeLayoutUpload.progressBarUpload.progress = current
        binding!!.relativeLayoutUpload.textViewRemaining.text = current.toString() + "/" + total
    }

    override fun onDestroy() {
        adapter!!.dispose()
        super.onDestroy()
    }

    companion object {

        // Request codes (just random numbers to help us track activity results
        // and permission requests)
        internal const val PICK_SINGLE_MEDIA = 121
        internal const val PICK_IMAGES = 123
        const val REQUEST_READ_STORAGE_PERMISSION = 1234
        private const val MAX_SELECTION = 10
    }
}
