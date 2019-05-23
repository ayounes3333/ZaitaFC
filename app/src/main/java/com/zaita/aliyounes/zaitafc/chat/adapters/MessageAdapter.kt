package com.zaita.aliyounes.zaitafc.chat.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.chat.model.DateMessage
import com.zaita.aliyounes.zaitafc.chat.model.Message
import com.zaita.aliyounes.zaitafc.databinding.*
import com.zaita.aliyounes.zaitafc.helpers.DateTimeUtils
import com.zaita.aliyounes.zaitafc.helpers.PrefUtils
import com.zaita.aliyounes.zaitafc.helpers.TextUtils
import durdinapps.rxfirebase2.RxFirebaseRecyclerAdapter

class MessageAdapter(private val context: Context) : RxFirebaseRecyclerAdapter<MessageAdapter.MessageViewHolder, Message>(Message::class.java) {

    open inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    inner class SelfTextViewHolder(var binding: ItemTextOutgoingBinding) : MessageViewHolder(binding.root)

    inner class OtherTextViewHolder(var binding: ItemTextIncommingBinding) : MessageViewHolder(binding.root)

    inner class DateMessageViewHolder(var binding: ItemConversationDateLayoutBinding) : MessageViewHolder(binding.root)

    inner class SelfImageViewHolder(var binding: ItemImageOutgoingBinding) : MessageViewHolder(binding.root)

    inner class OtherImageViewHolder(var binding: ItemImageIncommingBinding) : MessageViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        when (viewType) {
            0 -> {
                val binding = DataBindingUtil.inflate<ItemTextOutgoingBinding>(LayoutInflater.from(context), R.layout.item_text_outgoing, parent, false)
                return SelfTextViewHolder(binding)
            }

            1 -> {
                val binding1 = DataBindingUtil.inflate<ItemImageOutgoingBinding>(LayoutInflater.from(context), R.layout.item_image_outgoing, parent, false)
                return SelfImageViewHolder(binding1)
            }

            2 -> {
                val binding2 = DataBindingUtil.inflate<ItemTextIncommingBinding>(LayoutInflater.from(context), R.layout.item_text_incomming, parent, false)
                return OtherTextViewHolder(binding2)
            }

            3 -> {
                val binding3 = DataBindingUtil.inflate<ItemImageIncommingBinding>(LayoutInflater.from(context), R.layout.item_image_incomming, parent, false)
                return OtherImageViewHolder(binding3)
            }

            else -> {
                val binding4 = DataBindingUtil.inflate<ItemConversationDateLayoutBinding>(LayoutInflater.from(context), R.layout.item_conversation_date_layout, parent, false)
                return DateMessageViewHolder(binding4)
            }
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = items[position]

        when (holder.itemViewType) {
            0 -> {
                if (!TextUtils.isEmpty(message.messageDate))
                    (holder as SelfTextViewHolder).binding.lblDate.text = DateTimeUtils.getMessageDateTime(message.messageDate!!)
                (holder as SelfTextViewHolder).binding.lblFromMe.text = message.messageContent
            }

            1 -> {
                if (!TextUtils.isEmpty(message.messageDate))
                    (holder as SelfImageViewHolder).binding.lblDate.text = DateTimeUtils.getMessageDateTime(message.messageDate!!)
                FirebaseStorage.getInstance().reference.child(items[position].messageContent!!.substring(1)).downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(context)
                            .load(uri.toString())
                            .apply(RequestOptions().placeholder(R.drawable.placeholder))
                            .into((holder as SelfImageViewHolder).binding.imgFromMe)
                }.addOnFailureListener { it.printStackTrace() }
            }

            2 -> {
                if (!TextUtils.isEmpty(message.messageDate))
                    (holder as OtherTextViewHolder).binding.lblDate.text = DateTimeUtils.getMessageDateTime(message.messageDate!!)
                (holder as OtherTextViewHolder).binding.lblFromContact.text = message.messageContent
                holder.binding.lblName.text = message.senderName
            }

            3 -> {
                if (!TextUtils.isEmpty(message.messageDate))
                    (holder as OtherImageViewHolder).binding.lblDate.text = DateTimeUtils.getMessageDateTime(message.messageDate!!)
                (holder as OtherImageViewHolder).binding.lblName.text = message.senderName
                FirebaseStorage.getInstance().reference.child(items[position].messageContent!!.substring(1)).downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(context)
                            .load(uri.toString())
                            .apply(RequestOptions().placeholder(R.drawable.placeholder))
                            .into(holder.binding.imgFromContact)
                }.addOnFailureListener { it.printStackTrace() }
            }

            4 -> {
                val dateMessage = message as DateMessage
                (holder as DateMessageViewHolder).binding.date.text = dateMessage.date
            }
        }
    }

    override fun itemAdded(message: Message, s: String, i: Int) {

    }

    override fun itemChanged(message: Message, t1: Message, s: String, i: Int) {

    }

    override fun itemRemoved(message: Message, s: String, i: Int) {

    }

    override fun itemMoved(message: Message, s: String, i: Int, i1: Int) {

    }


    override fun getItemViewType(position: Int): Int {
        val message = items[position]

        if (message is DateMessage) {
            return 4
        }

        return if (message.contentType == Message.ContentTypes.TEXT) {
            if (message.senderId!!.equals(PrefUtils.Session.userId!!, ignoreCase = true)) {
                0
            } else {
                2
            }
        } else {
            if (message.senderId!!.equals(PrefUtils.Session.userId!!, ignoreCase = true)) {
                1
            } else {
                3
            }
        }
    }

    companion object {

        @Suppress("unused")
        fun dpToPx(context: Context, dp: Int): Int {
            val density = context.resources.displayMetrics.density
            return Math.round(dp.toFloat() * density)
        }
    }


}
