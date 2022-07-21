package com.learning.b38firebasedemo.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.learning.b38firebasedemo.R
import com.learning.b38firebasedemo.databinding.ActivityHomeBinding
import com.learning.b38firebasedemo.databinding.ActivityMainBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        binding.apply {
            txtDisplayName.text = bundle?.get("name").toString()
            txtEmail.text = bundle?.get("email").toString()

            Glide.with(this@HomeActivity)
                .load(bundle?.get("profileUrl").toString())
                .into(profileImage)
        }
    }
}