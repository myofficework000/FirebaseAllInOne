package com.learning.b38firebasedemo.crashanalytics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.learning.b38firebasedemo.R
import com.learning.b38firebasedemo.databinding.ActivityCrashAnalyticsDemoBinding
import java.lang.RuntimeException

class CrashAnalyticsDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrashAnalyticsDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrashAnalyticsDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnTestCrash.setOnClickListener {
            throw RuntimeException("Test a crash.....")
        }
    }
}