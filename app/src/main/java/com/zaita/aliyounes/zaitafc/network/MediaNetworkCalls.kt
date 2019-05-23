package com.zaita.aliyounes.zaitafc.network

import android.net.Uri
import android.util.Pair

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.zaita.aliyounes.zaitafc.chat.model.Media

import java.util.ArrayList

import durdinapps.rxfirebase2.RxFirebaseChildEvent
import durdinapps.rxfirebase2.RxFirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseStorage
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.CompletableOnSubscribe
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers

/*
    every static method in this class is a network call.
    In android, network calls are executed in a background thread.
    In this case i used RxJava2 with RxFirebase to communicate with the database.
    It is useful because it handles threading stuff for us in a comprehensive way.
 */

object MediaNetworkCalls {

    // the firebase database instance is used to communicate with firebase database.
    private val mDatabase = FirebaseDatabase.getInstance().reference

    // Create a storage reference from our app
    private val storageRef = FirebaseStorage.getInstance().reference

    // Observable is an RxJava class that holds an observable result with a data type.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to upload images to firebase storage.
    // FirebaseDatabase.setValue(...) is a method of Firebase library
    // that changes a value in the firebase database.
    fun uploadImages(images: List<Media>): Observable<Pair<Media, Int>> {

        val progress = intArrayOf(0)

        val toUpload = ArrayList<Media>()
        for (image in images) {
            if (image.type != Media.MediaTypes.ADD)
                toUpload.add(image)
        }
        return Observable.fromIterable(toUpload)
                .concatMap { image ->
                    val extension = image.path.substring(image.path.lastIndexOf('.'))
                    RxFirebaseStorage.putFile(storageRef.child("Images/" + image.id + extension), Uri.parse(image.path))
                            .flatMapObservable {
                                progress[0]++
                                image.path = storageRef.child("Images/" + image.id + extension).path
                                Observable.just(Pair(image, progress[0]))
                            }
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    // Completable is an RxJava class that holds an observable result without a data type.
    // Completable is only concerned with execution completion whether the task has
    // reach to completion or some error has occurred.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to add image to database after upload.
    // RxFirebaseDatabase.setValue(...) is a method of RxFirebase library
    // that changes a value in the firebase database and return an observable for us to perform the appropriate actions
    fun addImageToDB(postId: String, image: Media): Completable {
        //the first argument is the database path in witch we need to change a value in the firebase database.
        //the second argument is the new value in that path.
        val newKey = mDatabase.child("images").child(postId).push().key
        return RxFirebaseDatabase.setValue(mDatabase.child("images").child(postId).child(newKey!!), image)
    }

    // Observable is an RxJava class that holds an observable result with a data type.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to upload videos to firebase storage.
    // FirebaseDatabase.setValue(...) is a method of Firebase library
    // that changes a value in the firebase database.
    fun uploadVideos(videos: List<Media>): Observable<Pair<Media, Int>> {

        val progress = intArrayOf(0)

        val toUpload = ArrayList<Media>()
        for (video in videos) {
            if (video.type != Media.MediaTypes.ADD)
                toUpload.add(video)
        }
        return Observable.fromIterable(toUpload)
                .concatMap { video ->
                    val extension = video.path.substring(video.path.lastIndexOf('.'))
                    RxFirebaseStorage.putFile(storageRef.child("Videos/" + video.id + extension), Uri.parse(video.path))
                            .flatMapObservable {
                                progress[0]++
                                video.path = storageRef.child("Videos/" + video.id + extension).path
                                Observable.just(Pair(video, progress[0]))
                            }
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    // Completable is an RxJava class that holds an observable result without a data type.
    // Completable is only concerned with execution completion whether the task has
    // reach to completion or some error has occurred.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to add video to database after upload.
    // RxFirebaseDatabase.setValue(...) is a method of RxFirebase library
    // that changes a value in the firebase database and return an observable for us to perform the appropriate actions
    fun addVideoToDB(postId: String, image: Media): Completable {
        //the first argument is the database path in witch we need to change a value in the firebase database.
        //the second argument is the new value in that path.
        val newKey = mDatabase.child("videos").child(postId).push().key
        return RxFirebaseDatabase.setValue(mDatabase.child("videos").child(postId).child(newKey!!), image)
    }

    // Observable is an RxJava class that holds an observable result with a data type.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to upload best shots to firebase storage.
    // FirebaseDatabase.setValue(...) is a method of Firebase library
    // that changes a value in the firebase database.
    fun uploadNewsMedia(media: List<Media>): Observable<Pair<Media, Int>> {

        val progress = intArrayOf(0)

        val toUpload = ArrayList<Media>()
        for (NewsMedia in media) {
            if (NewsMedia.type != Media.MediaTypes.ADD)
                toUpload.add(NewsMedia)
        }
        return Observable.fromIterable(toUpload)
                .concatMap { NewsMedia ->
                    val extension = NewsMedia.path.substring(NewsMedia.path.lastIndexOf('.'))
                    RxFirebaseStorage.putFile(storageRef.child("NewsMedia/" + NewsMedia.id + extension), Uri.parse(NewsMedia.path))
                            .flatMapObservable {
                                progress[0]++
                                NewsMedia.path = storageRef.child("NewsMedia/" + NewsMedia.id + extension).path
                                Observable.just(Pair(NewsMedia, progress[0]))
                            }
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    // Completable is an RxJava class that holds an observable result without a data type.
    // Completable is only concerned with execution completion whether the task has
    // reach to completion or some error has occurred.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to add best shot to database after upload.
    // RxFirebaseDatabase.setValue(...) is a method of RxFirebase library
    // that changes a value in the firebase database and return an observable for us to perform the appropriate actions
    fun addNewsMediaToDB(postId: String, image: Media): Completable {
        //the first argument is the database path in witch we need to change a value in the firebase database.
        //the second argument is the new value in that path.
        val newKey = mDatabase.child("NewsMedia").child(postId).push().key
        return RxFirebaseDatabase.setValue(mDatabase.child("NewsMedia").child(postId).child(newKey!!), image)
    }

    // Flowable is an RxJava class that holds an observable result.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to get and monitor images from database
    // RxFirebaseDatabase.observeChildEvent(...) is a method of RxFirebase library
    // that observes a value in the firebase database and return an observable for us to perform the appropriate actions
    fun getImages(postId: String): Flowable<RxFirebaseChildEvent<DataSnapshot>> {
        return RxFirebaseDatabase.observeChildEvent(mDatabase.child("images").child(postId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    // Flowable is an RxJava class that holds an observable result.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to get and monitor videos from database
    // RxFirebaseDatabase.observeChildEvent(...) is a method of RxFirebase library
    // that observes a value in the firebase database and return an observable for us to perform the appropriate actions
    fun getVideos(postId: String): Flowable<RxFirebaseChildEvent<DataSnapshot>> {
        return RxFirebaseDatabase.observeChildEvent(mDatabase.child("videos").child(postId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    // Flowable is an RxJava class that holds an observable result.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to get and monitor best shots from database
    // RxFirebaseDatabase.observeChildEvent(...) is a method of RxFirebase library
    // that observes a value in the firebase database and return an observable for us to perform the appropriate actions
    fun getNwsMedia(postId: String): Flowable<RxFirebaseChildEvent<DataSnapshot>> {
        return RxFirebaseDatabase.observeChildEvent(mDatabase.child("NewsMedia").child(postId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    // Completable is an RxJava class that holds an observable result without a data type.
    // Completable is only concerned with execution completion whether the task has
    // reach to completion or some error has occurred.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to delete an image from database.
    // FirebaseReference.removeValue(...) is a method of RxFirebase library
    // that removes a value in the firebase database and return an observable for us to perform the appropriate actions
    fun deleteImage(postId: String, key: String): Completable {
        return Completable.create { e ->
            mDatabase.child("images").child(postId).child(key).removeValue().addOnSuccessListener { e.onComplete() }.addOnFailureListener { exception ->
                if (!e.isDisposed)
                    e.onError(exception)
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    // Completable is an RxJava class that holds an observable result without a data type.
    // Completable is only concerned with execution completion whether the task has
    // reach to completion or some error has occurred.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to delete a video from database.
    // FirebaseReference.removeValue(...) is a method of RxFirebase library
    // that removes a value in the firebase database and return an observable for us to perform the appropriate actions
    fun deleteVideo(postId: String, key: String): Completable {
        return Completable.create { e ->
            mDatabase.child("videos").child(postId).child(key).removeValue().addOnSuccessListener { e.onComplete() }.addOnFailureListener { exception ->
                if (!e.isDisposed)
                    e.onError(exception)
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    // Completable is an RxJava class that holds an observable result without a data type.
    // Completable is only concerned with execution completion whether the task has
    // reach to completion or some error has occurred.
    // See: https://medium.com/@rohitsingh14101992/rxjava-single-maybe-and-completable-8686db42bac8
    // this method is used to delete a best shot from database.
    // FirebaseReference.removeValue(...) is a method of RxFirebase library
    // that removes a value in the firebase database and return an observable for us to perform the appropriate actions
    fun deleteNewsMedia(postId: String, key: String): Completable {
        return Completable.create { e ->
            mDatabase.child("NewsMedia").child(postId).child(key).removeValue().addOnSuccessListener { e.onComplete() }.addOnFailureListener { exception ->
                if (!e.isDisposed)
                    e.onError(exception)
            }
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
