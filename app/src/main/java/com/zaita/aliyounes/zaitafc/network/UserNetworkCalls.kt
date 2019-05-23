package com.zaita.aliyounes.zaitafc.network

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.zaita.aliyounes.zaitafc.pojos.UserDetails
import com.zaita.aliyounes.zaitafc.pojos.UserRegistrationData
import durdinapps.rxfirebase2.RxFirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

@Suppress("UNCHECKED_CAST")
/**
 * Created by Lenovo on 11/29/2017.
 */

object UserNetworkCalls {
    private val mDatabase = FirebaseDatabase.getInstance().reference

    @Suppress("UNCHECKED_CAST")
    fun loginUser(email: String, password: String): Maybe<Boolean> {
        return RxFirebaseAuth.signInWithEmailAndPassword(FirebaseAuth.getInstance(), email, password)
                .flatMap({ authResult: AuthResult ->
                    if (authResult.user != null) {
                        RxFirebaseAuth.signInWithEmailAndPassword(FirebaseAuth.getInstance(), email, password)
                                .flatMap {Maybe.just<Boolean>(true)}
                    } else {
                        RxFirebaseAuth.signInWithEmailAndPassword(FirebaseAuth.getInstance(), email, password)
                                .flatMap {Maybe . error < Boolean >(Exception("Login Failed!"))}
                    }
                } as Function<AuthResult, Maybe<Boolean>>).observeOn(AndroidSchedulers.mainThread())
    }

    fun registerUser(data: UserRegistrationData): Maybe<Boolean> {
        return RxFirebaseAuth.createUserWithEmailAndPassword(FirebaseAuth.getInstance(), data.email, data.password)
                .flatMap({ authResult: AuthResult ->
                    if (authResult.user != null) {
                        RxFirebaseAuth.createUserWithEmailAndPassword(FirebaseAuth.getInstance(), data.email, data.password)
                                .flatMap { Maybe . just < Boolean >(true) }
                    } else {
                        RxFirebaseAuth.createUserWithEmailAndPassword(FirebaseAuth.getInstance(), data.email, data.password)
                                .flatMap { Maybe . error < Boolean >(Exception("Register Failed!")) }
                    }
                } as Function<AuthResult, Maybe<Boolean>>).observeOn(AndroidSchedulers.mainThread())
    }

    fun addUser(userId: String, user: UserDetails): Completable {
        return RxFirebaseDatabase.setValue(mDatabase.child("users").child(userId), user)
    }

    fun getUser(userId: String): Maybe<DataSnapshot> {
        return RxFirebaseDatabase.observeSingleValueEvent(mDatabase.child("users").child(userId))
    }

    val users: Flowable<RxFirebaseChildEvent<DataSnapshot>>
        get() = RxFirebaseDatabase.observeChildEvent(mDatabase.child("users"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
}
