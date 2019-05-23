package com.zaita.aliyounes.zaitafc.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.helpers.PrefUtils
import com.zaita.aliyounes.zaitafc.network.UserNetworkCalls
import com.zaita.aliyounes.zaitafc.pojos.UserDetails

import java.io.IOException
import java.util.regex.Pattern

import io.reactivex.MaybeObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class LoginActivity : AppCompatActivity() {

    private var textInputUserName: TextInputLayout? = null
    private var textInputPassword: TextInputLayout? = null
    private var compositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        compositeDisposable = CompositeDisposable()
        //Get Firebase auth instance
        setupViews()
    }

    private fun setupViews() {
        textInputUserName = findViewById(R.id.textInput_userName)
        textInputPassword = findViewById(R.id.textInput_password)
        val progressBarLoggingIn = findViewById<ProgressBar>(R.id.progressBar_loggingIn)
        findViewById<View>(R.id.button_login).setOnClickListener {
            if (checkInput()) {
                progressBarLoggingIn.visibility = View.VISIBLE
                //authenticate user
                UserNetworkCalls.loginUser(textInputUserName!!.editText!!.text.toString(), textInputPassword!!.editText!!.text.toString()).subscribe(object : MaybeObserver<Boolean> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable!!.add(d)
                    }

                    override fun onSuccess(aBoolean: Boolean) {

                        UserNetworkCalls.getUser(FirebaseAuth.getInstance().uid!!).subscribe(object : MaybeObserver<DataSnapshot> {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable!!.add(d)
                            }

                            override fun onSuccess(dataSnapshot: DataSnapshot) {
                                val user = dataSnapshot.getValue<UserDetails>(UserDetails::class.java)
                                if (user != null) {
                                    PrefUtils.Session.userEmail = user.email
                                    PrefUtils.Session.userName = user.name
                                    PrefUtils.Session.isAdmin = user.isAdmin
                                    PrefUtils.Session.userId = FirebaseAuth.getInstance().uid
                                    PrefUtils.Session.age = user.age
                                    PrefUtils.Session.genderId = user.genderId
                                    PrefUtils.Session.position = user.position
                                    PrefUtils.setBoolean(this@LoginActivity, PrefUtils.Prefs.IS_USER_LOGGED_IN, true)
                                    progressBarLoggingIn.visibility = View.GONE
                                    Toast.makeText(this@LoginActivity, "Welcome", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    finish()
                                }
                            }

                            override fun onError(e: Throwable) {
                                // print exception stack
                                e.printStackTrace()
                                if (e is IOException) {
                                    // this is most likely a connection error, so we need to show a message toast.
                                    Toast.makeText(this@LoginActivity, R.string.no_internet, Toast.LENGTH_SHORT).show()
                                } else {
                                    // we need to show the exception details in a message toast.
                                    Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onComplete() {

                            }
                        })
                    }

                    override fun onError(e: Throwable) {
                        progressBarLoggingIn.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        if (textInputPassword!!.editText!!.text.toString().length < 6) {
                            textInputPassword!!.error = getString(R.string.minimum_password)
                        } else {
                            Toast.makeText(this@LoginActivity, getString(R.string.auth_failed), Toast.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                    }

                    override fun onComplete() {

                    }
                })
            }
        }
        findViewById<View>(R.id.button_signUp).setOnClickListener { startActivity(Intent(this@LoginActivity, RegisterActivity::class.java)) }
    }

    private fun checkInput(): Boolean {
        if (textInputUserName == null || textInputUserName!!.editText == null)
            return false
        else if (textInputUserName!!.editText!!.text.isEmpty() || !isEmailValid(textInputUserName!!.editText!!.text.toString())) {
            Toast.makeText(this@LoginActivity, "Empty or Invalid Email Address", Toast.LENGTH_SHORT).show()
            return false
        }
        if (textInputPassword == null || textInputPassword!!.editText == null)
            return false
        else if (textInputPassword!!.editText!!.text.isEmpty()) {
            Toast.makeText(this@LoginActivity, "Password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onDestroy() {
        compositeDisposable!!.dispose()
        super.onDestroy()
    }

    companion object {
        fun isEmailValid(email: String): Boolean {

            @Suppress("RegExpRedundantEscape")
            val expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
            val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(email)
            return matcher.matches()
        }
    }
}
