package com.zaita.aliyounes.zaitafc.chat.adapters

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.chat.activities.ConversationActivity
import com.zaita.aliyounes.zaitafc.chat.model.ChatDialog
import com.zaita.aliyounes.zaitafc.databinding.ItemChatLayoutBinding
import durdinapps.rxfirebase2.RxFirebaseRecyclerAdapter


class ChatsAdapter(private val context: Context) : RxFirebaseRecyclerAdapter<ChatsAdapter.ChatsViewHolder, ChatDialog>(ChatDialog::class.java) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val binding = DataBindingUtil.inflate<ItemChatLayoutBinding>(LayoutInflater.from(context), R.layout.item_chat_layout, parent, false)
        return ChatsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        val dialog = items[position]
        holder.binding.textViewName.text = dialog.chatName
        holder.binding.root.setOnClickListener {
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra("chatId", items[holder.adapterPosition].chatId)
            intent.putExtra("chatName", items[holder.adapterPosition].chatName)
            context.startActivity(intent)
        }
    }

    override fun itemAdded(chatDialog: ChatDialog, s: String, i: Int) {

    }

    override fun itemChanged(chatDialog: ChatDialog, t1: ChatDialog, s: String, i: Int) {

    }

    override fun itemRemoved(chatDialog: ChatDialog, s: String, i: Int) {

    }

    override fun itemMoved(chatDialog: ChatDialog, s: String, i: Int, i1: Int) {

    }

    inner class ChatsViewHolder(var binding: ItemChatLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
