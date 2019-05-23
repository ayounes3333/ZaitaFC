package com.zaita.aliyounes.zaitafc.network

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.zaita.aliyounes.zaitafc.pojos.Post

import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


object NewsNetworkCalls {
    private val mDatabase = FirebaseDatabase.getInstance().reference

    val newsPosts: Flowable<RxFirebaseChildEvent<DataSnapshot>>
        get() = RxFirebaseDatabase.observeChildEvent(mDatabase.child("posts"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())


    fun addNewsPost(post: Post): Observable<String> {
        val key = mDatabase.child("posts").push().key
        return Observable.create { emitter ->
            if (key != null) {
                mDatabase.child("posts").child(key).setValue(post).addOnSuccessListener { emitter.onNext(key) }.addOnFailureListener { exception ->
                    if (!emitter.isDisposed)
                        emitter.onError(exception)
                }
            } else {
                emitter.onError(Exception("Key is null"))
            }
        }
    }
}
