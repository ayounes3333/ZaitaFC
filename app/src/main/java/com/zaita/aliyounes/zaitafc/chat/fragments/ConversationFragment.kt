package com.zaita.aliyounes.zaitafc.chat.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Pair
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.erikagtierrez.multiple_media_picker.Gallery
import com.zaita.aliyounes.zaitafc.BuildConfig
import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.chat.adapters.MessageAdapter
import com.zaita.aliyounes.zaitafc.chat.model.Media
import com.zaita.aliyounes.zaitafc.chat.model.Message
import com.zaita.aliyounes.zaitafc.chat.network.MessagesNetworkCalls
import com.zaita.aliyounes.zaitafc.databinding.FragmentConversationBinding
import com.zaita.aliyounes.zaitafc.helpers.DateTimeUtils
import com.zaita.aliyounes.zaitafc.helpers.PrefUtils
import com.zaita.aliyounes.zaitafc.network.MediaNetworkCalls
import io.reactivex.CompletableObserver
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.io.File
import java.io.IOException
import java.util.*


class ConversationFragment : Fragment() {
    private var binding: FragmentConversationBinding? = null
    private var messageAdapter: MessageAdapter? = null
    private val compositeDisposable = CompositeDisposable()
    private var chatId: String? = null
    private var chatName: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversation, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            chatId = arguments!!.getString("chatId")
            chatName = arguments!!.getString("chatName")
            setupViews()
            setupRecyclerViews()
            populateFirebaseData()
        }
    }

    private fun setupViews() {
        binding!!.imbSend.isEnabled = false
        binding!!.txtMessage.setOnEditorActionListener { _: TextView, i: Int, _: KeyEvent ->
            return@setOnEditorActionListener if (i == EditorInfo.IME_ACTION_DONE) {
                binding!!.imbSend.performClick()
                true
            } else false
        }

        binding!!.imbSend.setOnClickListener {
            if (binding!!.txtMessage.text.isNotEmpty() && !binding!!.txtMessage.text.toString().equals("", ignoreCase = true)) {

                val message = Message()
                message.senderId = PrefUtils.Session.userId
                message.senderName = PrefUtils.Session.userName
                message.chatId = chatId
                message.chatName = chatName
                message.contentType = Message.ContentTypes.TEXT
                message.messageContent = binding!!.txtMessage.text.toString()
                message.messageDate = DateTimeUtils.currentDbDateString
                binding!!.sending.visibility = View.VISIBLE
                MessagesNetworkCalls.sendMessage(message).subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onComplete() {
                        binding!!.sending.visibility = View.GONE
                        if (BuildConfig.DEBUG) {
                            Toast.makeText(context, R.string.message_sent, Toast.LENGTH_SHORT).show()
                        }
                        binding!!.rvMessageList.smoothScrollToPosition(binding!!.rvMessageList.adapter!!.itemCount)
                    }

                    override fun onError(e: Throwable) {
                        binding!!.sending.visibility = View.GONE
                        Toast.makeText(context, R.string.error_failed_to_send_message, Toast.LENGTH_SHORT).show()
                        // print exception stack
                        e.printStackTrace()
                        if (e is IOException) {
                            // this is most likely a connection error, so we need to show a message toast.
                            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
                        } else {
                            // we need to show the exception details in a message toast.
                            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
                binding!!.txtMessage.text.clear()
            }
        }
        binding!!.txtMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                binding!!.imbSend.isEnabled = binding!!.txtMessage.text.toString().trim { it <= ' ' }.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
        binding!!.imbAttach.setOnClickListener {
            val intent = Intent(context, Gallery::class.java)
            // Set the title
            intent.putExtra("title", "Select media")
            // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
            intent.putExtra("mode", 2)
            intent.putExtra("maxSelection", 1) // Optional
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

    }

    private fun setupRecyclerViews() {
        messageAdapter = MessageAdapter(this.activity as AppCompatActivity)
        val linearLayoutManager = LinearLayoutManager(this.activity)
        binding!!.rvMessageList.layoutManager = linearLayoutManager
        binding!!.rvMessageList.adapter = messageAdapter
        binding!!.rvMessageList.setHasFixedSize(true)
        binding!!.rvMessageList.setItemViewCacheSize(20)
        binding!!.rvMessageList.smoothScrollToPosition(binding!!.rvMessageList.adapter!!.itemCount)
        binding!!.rvMessageList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val i = linearLayoutManager.findFirstVisibleItemPosition()
                if (DateTimeUtils.fromDbDateString(messageAdapter!!.items[i].messageDate!!) != null) {

                    if (DateTimeUtils.fromDbDateString(messageAdapter!!.items[i].messageDate!!)!!.after(DateTimeUtils.todayDate)) {
                        binding!!.lblDateBar.setText(R.string.today)
                    } else {
                        binding!!.lblDateBar.text = DateTimeUtils.getDayStringFromDbString(messageAdapter!!.items[i].messageDate!!)
                    }
                }
            }
        })
        if (messageAdapter!!.items.size == 0) {
            binding!!.empty.visibility = View.VISIBLE
        } else {
            binding!!.empty.visibility = View.GONE
        }
    }


    private fun populateFirebaseData() {
        compositeDisposable.add(MessagesNetworkCalls.getMessages(chatId!!).subscribe({ message ->
            //Success
            messageAdapter!!.manageChildItem(message)
            if (messageAdapter!!.items.size == 0) {
                binding!!.empty.visibility = View.VISIBLE
            } else {
                binding!!.empty.visibility = View.GONE
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
            }
        }))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            val selectionResult = data!!.getStringArrayListExtra("result")
            val images = ArrayList<Media>()
            for (path in selectionResult) {
                images.add(Media(Uri.fromFile(File(path)).toString(), PrefUtils.Session.userId!!, Media.MediaTypes.VIDEO))
            }
            val message = Message()
            message.senderId = PrefUtils.Session.userId
            message.senderName = PrefUtils.Session.userName
            message.chatId = chatId
            message.chatName = chatName
            message.contentType = Message.ContentTypes.IMAGE
            message.messageDate = DateTimeUtils.currentDbDateString
            binding!!.sending.visibility = View.VISIBLE
            MediaNetworkCalls.uploadImages(images).subscribe(object : Observer<Pair<Media, Int>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(mediaIntegerPair: Pair<Media, Int>) {
                    binding!!.sending.visibility = View.GONE
                    message.messageContent = mediaIntegerPair.first.path
                    MessagesNetworkCalls.sendMessage(message).subscribe(object : CompletableObserver {
                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }

                        override fun onComplete() {
                            if (BuildConfig.DEBUG) {
                                Toast.makeText(context, R.string.message_sent, Toast.LENGTH_SHORT).show()
                            }
                            binding!!.rvMessageList.smoothScrollToPosition(binding!!.rvMessageList.adapter!!.itemCount)
                        }

                        override fun onError(e: Throwable) {
                            // print exception stack
                            Toast.makeText(context, R.string.error_failed_to_send_message, Toast.LENGTH_SHORT).show()
                            e.printStackTrace()
                            if (e is IOException) {
                                // this is most likely a connection error, so we need to show a message toast.
                                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
                            } else {
                                // we need to show the exception details in a message toast.
                                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }

                override fun onError(e: Throwable) {
                    binding!!.sending.visibility = View.GONE
                    // print exception stack
                    Toast.makeText(context, R.string.error_failed_to_send_message, Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                    if (e is IOException) {
                        // this is most likely a connection error, so we need to show a message toast.
                        Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
                    } else {
                        // we need to show the exception details in a message toast.
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onComplete() {

                }
            })
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    companion object {

        private const val PICK_IMAGE_REQUEST = 10
        val TAG: String = ConversationFragment::class.java.simpleName
    }
}
