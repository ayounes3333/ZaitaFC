package com.zaita.aliyounes.zaitafc.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.liuguangqiang.ipicker.IPicker
import com.squareup.picasso.Picasso
import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.helpers.PrefUtils
import com.zaita.aliyounes.zaitafc.network.UserNetworkCalls
import com.zaita.aliyounes.zaitafc.pojos.UserRegistrationData

import java.io.IOException

import io.reactivex.CompletableObserver
import io.reactivex.MaybeObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class FillInfoActivity : AppCompatActivity() {

    private var radioButtonMale: RadioButton? = null
    private var radioButtonFemale: RadioButton? = null
    private var textInputLayoutAge: TextInputLayout? = null
    private var textInputLayoutPosition: TextInputLayout? = null
    private var imageViewProfile: ImageView? = null

    private val compositeDisposable = CompositeDisposable()

    private var userRegistrationData: UserRegistrationData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fill_info)
        if (intent != null) {
            userRegistrationData = intent.getParcelableExtra("userRegistrationData")
        }
        setupViews()
    }

    private fun setupViews() {
        radioButtonMale = findViewById(R.id.radioButton_male)
        radioButtonFemale = findViewById(R.id.radioButton_female)
        textInputLayoutAge = findViewById(R.id.textInput_age)
        textInputLayoutPosition = findViewById(R.id.textInput_position)
        imageViewProfile = findViewById(R.id.imageView_profile)
        IPicker.setLimit(1)
        IPicker.setCropEnable(true)
        IPicker.setOnSelectedListener { paths ->
            Picasso.with(this@FillInfoActivity)
                    .load(paths[0])
                    .fit()
                    .placeholder(R.drawable.ic_empty_image)
                    .into(imageViewProfile)
            //TODO: Update Photo
        }
        imageViewProfile!!.setOnClickListener { IPicker.open(this@FillInfoActivity) }
        findViewById<View>(R.id.button_save).setOnClickListener {
            if (checkData()) {
                UserNetworkCalls.registerUser(userRegistrationData!!).subscribe(object : MaybeObserver<Boolean> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(aBoolean: Boolean) {
                        UserNetworkCalls.addUser(FirebaseAuth.getInstance().uid!!, userRegistrationData!!.asUserDetails()).subscribe(object : CompletableObserver {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onComplete() {
                                PrefUtils.Session.userEmail = userRegistrationData!!.email
                                PrefUtils.Session.userName = userRegistrationData!!.name
                                PrefUtils.Session.userId = FirebaseAuth.getInstance().uid
                                redirectToMainActivity()
                                finish()
                            }

                            override fun onError(e: Throwable) {
                                if (e is IOException) {
                                    Toast.makeText(this@FillInfoActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@FillInfoActivity, e.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        if (e is IOException) {
                            Toast.makeText(this@FillInfoActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@FillInfoActivity, e.message, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onComplete() {

                    }
                })
            }
        }
    }

    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun checkData(): Boolean {
        if (textInputLayoutAge!!.editText != null && textInputLayoutPosition!!.editText != null) {
            if (textInputLayoutPosition!!.editText!!.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
                Toast.makeText(this@FillInfoActivity, R.string.empty_position, Toast.LENGTH_SHORT).show()
                return false
            } else {
                userRegistrationData!!.position = textInputLayoutPosition!!.editText!!.text.toString()
            }
            if (textInputLayoutAge!!.editText!!.text.toString().trim { it <= ' ' }.equals("", ignoreCase = true)) {
                Toast.makeText(this@FillInfoActivity, R.string.empty_age, Toast.LENGTH_SHORT).show()
                return false
            } else {
                userRegistrationData!!.age = Integer.parseInt(textInputLayoutAge!!.editText!!.text.toString())
            }
            if (!radioButtonFemale!!.isChecked && !radioButtonMale!!.isChecked) {
                Toast.makeText(this@FillInfoActivity, R.string.no_gender, Toast.LENGTH_SHORT).show()
                return false
            } else {
                userRegistrationData!!.genderId = if (radioButtonMale!!.isChecked) 1 else 2
            }
            return true
        }
        return false
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
