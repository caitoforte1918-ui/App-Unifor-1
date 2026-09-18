package com.lithub.app.student

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lithub.app.R

class BookReadingActivity : AppCompatActivity() {

    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_reading)

        val sharedPref = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        isDarkMode = sharedPref.getBoolean("isDarkMode", false)
        updateTheme()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnTheme = findViewById<ImageView>(R.id.btnTheme)
        btnTheme.setOnClickListener {
            isDarkMode = !isDarkMode
            sharedPref.edit().putBoolean("isDarkMode", isDarkMode).apply()
            updateTheme()
        }

        findViewById<Button>(R.id.btnNextReading).setOnClickListener {
            startActivity(Intent(this, BookEndChapterActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("ThemePrefs", MODE_PRIVATE)
        val currentMode = sharedPref.getBoolean("isDarkMode", false)
        if (currentMode != isDarkMode) {
            isDarkMode = currentMode
            updateTheme()
        }
    }

    private fun updateTheme() {
        val scrollView = findViewById<ScrollView>(R.id.scrollViewReading)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvPage1 = findViewById<TextView>(R.id.tvPage1)
        val tvContent1 = findViewById<TextView>(R.id.tvContent1)
        val tvPage2 = findViewById<TextView>(R.id.tvPage2)
        val tvContent2 = findViewById<TextView>(R.id.tvContent2)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnTheme = findViewById<ImageView>(R.id.btnTheme)

        if (isDarkMode) {
            scrollView.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_dark))
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvPage1.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvContent1.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvPage2.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvContent2.setTextColor(ContextCompat.getColor(this, R.color.white))
            btnBack.setColorFilter(ContextCompat.getColor(this, R.color.white))
            btnTheme.setColorFilter(ContextCompat.getColor(this, R.color.white))
        } else {
            scrollView.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_light))
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
            tvPage1.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
            tvContent1.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
            tvPage2.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
            tvContent2.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
            btnBack.setColorFilter(ContextCompat.getColor(this, R.color.primary))
            btnTheme.setColorFilter(ContextCompat.getColor(this, R.color.primary))
        }
    }
}
