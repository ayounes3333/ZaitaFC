package com.zaita.aliyounes.zaitafc.chat.network

import android.service.autofill.UserData
import com.zaita.aliyounes.zaitafc.chat.model.ChatDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zaita.aliyounes.zaitafc.helpers.PrefUtils
import com.zaita.aliyounes.zaitafc.pojos.UserDetails

import java.util.ArrayList

import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Lenovo on 11/29/2017.
 */

/*
    every static method in this class is a network call.
    In android, network calls are executed in a background thread.
    In this case i used RxJava2 with RxFirebase to communicate with the database.
    It is useful because it handles threading stuff for us in a comprehensive way.
 */

object ChatsNetworkCalls {

    // the firebase database instance is used to communicate with firebase database.
    private val mDatabase = FirebaseDatabase.getInstance().reference

    // Flowable is an RxJava class that holds an observable result.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to get and monitor user chats from database
    // RxFirebaseDatabase.observeChildEvent(...) is a method of RxFirebase library
    // that observes a value in the firebase database and return an observable for us to perform the appropriate actions
    val chats: Flowable<RxFirebaseChildEvent<DataSnapshot>>
        get() = RxFirebaseDatabase.observeChildEvent(mDatabase.child("userChats").child(PrefUtils.Session.userId!!))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    // Completable is an RxJava class that holds an observable result without a data type.
    // Completable is only concerned with execution completion whether the task has
    // reach to completion or some error has occurred.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to add a chat to database.
    // RxFirebaseAuth.setValue(...) is a method of RxFirebase library
    // that changes a value in the firebase database and return an observable for us to perform the appropriate actions
    fun createChat(userID:String, userData: UserDetails): Completable {
        val key = mDatabase.child("chats").push().key
        val dialog = ChatDialog()
        dialog.chatId = userID
        dialog.chatName = userData.name

        val user = UserDetails(PrefUtils.Session.userEmail!!, PrefUtils.Session.userName!!)

        val createChat = RxFirebaseDatabase.setValue(mDatabase.child("chats").child(key?:""), dialog)
        val addChatToUser = RxFirebaseDatabase.setValue(mDatabase.child("userChats").child(PrefUtils.Session.userId!!).child(dialog.chatId!!), dialog)
        val addUserToChat = RxFirebaseDatabase.setValue(mDatabase.child("chatUsers").child(key?:"").child(PrefUtils.Session.userId!!).child(dialog.chatId!!), user)
        val actions = ArrayList<Completable>()
        actions.add(createChat)
        actions.add(addChatToUser)
        actions.add(addUserToChat)

        return Completable.concat(actions)
    }
    // Completable is an RxJava class that holds an observable result without a data type.
    // Completable is only concerned with execution completion whether the task has
    // reach to completion or some error has occurred.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to add a chat to database.
    // RxFirebaseAuth.setValue(...) is a method of RxFirebase library
    // that changes a value in the firebase database and return an observable for us to perform the appropriate actions
    /*fun createGroup(name:String, users:List<UserDetails>): Completable {
        val dialog = ChatDialog()
        dialog.chatId = stadiumId
        dialog.chatName = stadiumName

        val user = UserDetails(PrefUtils.Session.userEmail!!, PrefUtils.Session.userName!!)

        val createChat = RxFirebaseDatabase.setValue(mDatabase.child("chats").child(stadiumId), dialog)
        val addChatToUser = RxFirebaseDatabase.setValue(mDatabase.child("userChats").child(PrefUtils.Session.userId!!).child(dialog.chatId!!), dialog)
        val addUserToChat = RxFirebaseDatabase.setValue(mDatabase.child("chatUsers").child(stadiumId).child(PrefUtils.Session.userId!!).child(dialog.chatId!!), user)
        val actions = ArrayList<Completable>()
        actions.add(createChat)
        actions.add(addChatToUser)
        actions.add(addUserToChat)

        return Completable.concat(actions)
    }*/
}
