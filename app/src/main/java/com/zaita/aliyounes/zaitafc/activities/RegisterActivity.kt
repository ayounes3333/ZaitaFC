package com.zaita.aliyounes.zaitafc.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.zaita.aliyounes.zaitafc.R
import com.zaita.aliyounes.zaitafc.helpers.DateTimeUtils
import com.zaita.aliyounes.zaitafc.pojos.UserRegistrationData

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    private var textInputUserName: TextInputLayout? = null
    private var textInputPassword: TextInputLayout? = null
    private var textInputBirthday: TextInputLayout? = null
    private var textInputFullName: TextInputLayout? = null
    private var textInputConfirmPassword: TextInputLayout? = null

    private var userRegistrationData: UserRegistrationData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        userRegistrationData = UserRegistrationData()
        setupViews()
    }

    private fun setupViews() {
        textInputUserName = findViewById(R.id.textInput_userName)
        textInputPassword = findViewById(R.id.textInput_password)
        textInputBirthday = findViewById(R.id.textInput_birthday)
        textInputFullName = findViewById(R.id.textInput_fullName)
        textInputConfirmPassword = findViewById(R.id.textInput_confirmPassword)

        if (textInputBirthday!!.editText != null) {
            val myCalendar = Calendar.getInstance()
            val onDateSetListener = { _:View, year:Int, month:Int, dayOfMonth:Int ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, month)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val dateFormat = "EEEE, dd/MM/yyyy" //In which you need put here
                val sdf = SimpleDateFormat(dateFormat, Locale.US)
                textInputBirthday!!.editText!!.setText(sdf.format(myCalendar.time))
                userRegistrationData!!.birthday = DateTimeUtils.toDayDateString(myCalendar.time)
            }

            textInputBirthday!!.editText!!.setOnClickListener {
                val datePickerDialog = DatePickerDialog(this@RegisterActivity, onDateSetListener,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datePickerDialog.show()

            }
        }
        findViewById<View>(R.id.button_join).setOnClickListener {
            if (checkInput()) {
                val intent = Intent(this@RegisterActivity, FillInfoActivity::class.java)
                intent.putExtra("userRegistrationData", userRegistrationData)
                startActivity(intent)
            }
        }

    }

    private fun checkInput(): Boolean {
        if (textInputUserName == null || textInputUserName!!.editText == null)
            return false
        else if (textInputUserName!!.editText!!.text.isEmpty() || !isEmailValid(textInputUserName!!.editText!!.text.toString())) {
            Toast.makeText(this@RegisterActivity, "Empty or Invalid Email Address", Toast.LENGTH_SHORT).show()
            return false
        } else
            userRegistrationData!!.email = textInputUserName!!.editText!!.text.toString()
        if (textInputPassword == null || textInputPassword!!.editText == null)
            return false
        else if (textInputPassword!!.editText!!.text.isEmpty()) {
            Toast.makeText(this@RegisterActivity, "Password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (textInputConfirmPassword == null || textInputConfirmPassword!!.editText == null)
            return false
        else if (textInputConfirmPassword!!.editText!!.text.isEmpty() || !textInputConfirmPassword!!.editText!!.text.toString().equals(textInputConfirmPassword!!.editText!!.text.toString(), ignoreCase = true)) {
            Toast.makeText(this@RegisterActivity, "Confirm your Password", Toast.LENGTH_SHORT).show()
            return false
        } else {
            userRegistrationData!!.password = textInputPassword!!.editText!!.text.toString()
        }
        if (textInputFullName == null || textInputFullName!!.editText == null)
            return false
        else if (textInputFullName!!.editText!!.text.isEmpty()) {
            Toast.makeText(this@RegisterActivity, "Full Name is required", Toast.LENGTH_SHORT).show()
            return false
        } else
            userRegistrationData!!.name = textInputFullName!!.editText!!.text.toString()
        if (textInputBirthday == null || textInputBirthday!!.editText == null)
            return false
        else if (textInputBirthday!!.editText!!.text.isEmpty()) {
            Toast.makeText(this@RegisterActivity, "Birthday is required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
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
