package com.example.noteapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_splash)

            Handler(Looper.getMainLooper()).postDelayed({
                goToHomeActivity()
            }, 3500L)
        }

        private fun goToHomeActivity() {
            Intent(this, HomeActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }
    }