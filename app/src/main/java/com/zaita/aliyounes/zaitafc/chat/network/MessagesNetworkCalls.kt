package com.zaita.aliyounes.zaitafc.chat.network

import com.zaita.aliyounes.zaitafc.chat.model.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/*
    every static method in this class is a network call.
    In android, network calls are executed in a background thread.
    In this case i used RxJava2 with RxFirebase to communicate with the database.
    It is useful because it handles threading stuff for us in a comprehensive way.
 */

object MessagesNetworkCalls {

    // the firebase database instance is used to communicate with firebase database.
    private val mDatabase = FirebaseDatabase.getInstance().reference

    // Completable is an RxJava class that holds an observable result without a data type.
    // Completable is only concerned with execution completion whether the task has
    // reach to completion or some error has occurred.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to add a message to database.
    // RxFirebaseAuth.setValue(...) is a method of RxFirebase library
    // that changes a value in the firebase database and return an observable for us to perform the appropriate actions
    fun sendMessage(message: Message): Completable {
        val key = mDatabase.child("messages").child(message.chatId!!).push().key
        return RxFirebaseDatabase.setValue(mDatabase.child("messages").child(message.chatId!!).child(key!!), message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    // Flowable is an RxJava class that holds an observable result.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to get and monitor messages from database
    // RxFirebaseDatabase.observeChildEvent(...) is a method of RxFirebase library
    // that observes a value in the firebase database and return an observable for us to perform the appropriate actions
    fun getMessages(chatId: String): Flowable<RxFirebaseChildEvent<DataSnapshot>> {
        return RxFirebaseDatabase.observeChildEvent(mDatabase.child("messages").child(chatId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
