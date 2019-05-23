package com.zaita.aliyounes.zaitafc.chat.adapters

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.pojos.UserDetails
import durdinapps.rxfirebase2.RxFirebaseRecyclerAdapter
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Ali Younes on 8/11/2017.
 */

class UsersAdapter(private val context: Context, @Suppress("unused") private val isAdmin: Boolean) : RxFirebaseRecyclerAdapter<UsersAdapter.UserViewHolder, UserDetails>(UserDetails::class.java) {
    override fun itemMoved(p0: UserDetails?, p1: String?, p2: Int, p3: Int) {

    }

    override fun itemAdded(p0: UserDetails?, p1: String?, p2: Int) {

    }

    override fun itemChanged(p0: UserDetails?, p1: UserDetails?, p2: String?, p3: Int) {

    }

    override fun itemRemoved(p0: UserDetails?, p1: String?, p2: Int) {

    }

    //List of getItems() to show
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): UserViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_user_layout, viewGroup, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(postViewHolder: UserViewHolder, position: Int) {
        val user = items[position]
        postViewHolder.bind(user)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textViewUserName: TextView = itemView.findViewById(R.id.textView_postTitle)
        private val textViewUserAge: TextView = itemView.findViewById(R.id.textView_postBody)
        private val textViewIsAdmin: TextView = itemView.findViewById(R.id.textView_postDate)
        private val cardViewUser: CardView = itemView.findViewById(R.id.cardView_post)

        //Bind data to List item
        internal fun bind(user: UserDetails) {
            textViewUserName.text = user.name
            textViewUserAge.text = user.age.toString()
            textViewIsAdmin.text = if(user.isAdmin) context.getString(R.string.admin) else context.getString(R.string.user)
            cardViewUser.setOnClickListener {
                //Add action for any user item click
            }
        }
    }

    fun dispose() {
        compositeDisposable.dispose()
    }
}
