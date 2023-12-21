package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.noteapp.databinding.ActivityMainBinding

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        // Find views by their IDs
        val tGetMyNote: TextView = findViewById(R.id.tGetMyNote)
        val iPanah: ImageView = findViewById(R.id.iPanah)

        // Set click listener for the TextView
        tGetMyNote.setOnClickListener {
            navigateToMainActivity()
        }
        // Set click listener for the ImageView
        iPanah.setOnClickListener {
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}