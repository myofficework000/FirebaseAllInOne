package com.learning.b38firebasedemo.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.learning.b38firebasedemo.R
import com.learning.b38firebasedemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var googleSignInClient: GoogleSignInClient

    var isLogin = false
    val REQ_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        googleSignInOptions()
    }

    private fun googleSignInOptions() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(CLIENT_ID)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.signInButton.setOnClickListener {
            signInUsingGoogle()
        }
    }

    private fun signInUsingGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQ_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credentials)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    intent.apply {
                        putExtra("name", account.displayName)
                        putExtra("email", account.email)
                        putExtra("profileUrl", account.photoUrl)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun initViews() {
        binding.radioGroup.setOnCheckedChangeListener(this)
        binding.btnLogin.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            if (isLogin) {
                makeLogin()
            } else {
                register()
            }
        }
        binding.registerByPhone.setOnClickListener {
            startActivity(Intent(this, PhoneRegisterActivity::class.java))
        }
    }

    private fun register() {
        binding.apply {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@MainActivity,
                            "Registered successfully!!",
                            Toast.LENGTH_SHORT
                        ).show()

                        if (firebaseAuth.currentUser != null) {
                            firebaseUser = firebaseAuth.currentUser as FirebaseUser
                        }
                        sendEmailVerification()
                        binding.progress.visibility = View.GONE
                    } else {
                        binding.progress.visibility = View.GONE
                        Toast.makeText(
                            this@MainActivity,
                            "Registration got failed!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun sendEmailVerification() {
        firebaseUser.let {
            firebaseUser.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity,
                        "Verification email send!!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to send email for verification",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun makeLogin() {
        binding.apply {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@MainActivity,
                            "Login successfully!!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progress.visibility = View.GONE
                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Login got failed!!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.progress.visibility = View.GONE
                    }
                }
        }
    }

    override fun onCheckedChanged(group: RadioGroup, checkId: Int) {
        val checkRadioButton = group.findViewById<RadioButton>(group.checkedRadioButtonId)

        checkRadioButton?.let {
            when (checkRadioButton.id) {
                R.id.loginRadioBtn -> {
                    binding.btnLogin.text = getString(R.string.login)
                    isLogin = true
                }
                else -> {
                    binding.btnLogin.text = getString(R.string.register)
                    isLogin = false
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth = Firebase.auth
        if (this::firebaseUser.isInitialized) {
            if (firebaseAuth.currentUser != null && firebaseUser != firebaseAuth) {
                firebaseUser = firebaseAuth.currentUser as FirebaseUser
            }
        }
    }

    companion object {
        const val CLIENT_ID =
            "803043203869-c64qt72vi21vdiofr7n13cgah3sbk3mi.apps.googleusercontent.com"
    }
}