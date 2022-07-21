package com.learning.b38firebasedemo.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.learning.b38firebasedemo.R
import com.learning.b38firebasedemo.databinding.ActivityPhoneRegisterActviityBinding
import java.util.concurrent.TimeUnit

class PhoneRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneRegisterActviityBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storeVerificationId: String
    private lateinit var resentToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var number: String = ""
    var isOTPGenerated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneRegisterActviityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        initView()
    }

    private fun initView() {
        binding.btnSendOTP.setOnClickListener {
            sendOTP()
        }

        callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Log.i("tag", "onVerificationCompleted called")
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                Log.i("tag", "onVerificationFailed called")
            }

            override fun onCodeSent(
                verficationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storeVerificationId = verficationId
                resentToken = token
                binding.apply {
                    edtMobile.hint = getString(R.string.enter_otp)
                    btnSendOTP.text = getString(R.string.verify_otp)
                    isOTPGenerated = true
                    btnSendOTP.setOnClickListener {
                        if (isOTPGenerated) {
                            verifyOTP()
                        }
                    }

                }
            }
        }
    }

    private fun verifyOTP() {
        val credential: PhoneAuthCredential =
            PhoneAuthProvider.getCredential(
                storeVerificationId,
                binding.edtMobile.text.toString()
            )
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Mobile number verified!!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                Toast.makeText(this, "Wrong OTP!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendOTP() {
        number = binding.edtMobile.text.toString()
        if (number.isNotEmpty()) {
            number = "+91$number"
            sendVerificationCode()
        } else {
            Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.i("tag", "Auth created!!")
    }
}