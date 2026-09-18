package com.lithub.app.student

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lithub.app.R

class BookEndChapterActivity : AppCompatActivity() {

    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_end_chapter)

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

        val previousBtn = findViewById<Button>(R.id.previousBtn)
        previousBtn.setOnClickListener {
            startActivity(Intent(this, BookReadingActivity::class.java))
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
        val rootLayout = findViewById<LinearLayout>(R.id.rootLayoutEndChapter)
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvPageInfo = findViewById<TextView>(R.id.tvPageInfo)
        val tvContent = findViewById<TextView>(R.id.tvContent)
        val tvEndChapter = findViewById<TextView>(R.id.tvEndChapter)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnTheme = findViewById<ImageView>(R.id.btnTheme)

        if (isDarkMode) {
            rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_dark))
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvPageInfo.setTextColor(ContextCompat.getColor(this, R.color.gray_light))
            tvContent.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvEndChapter.setTextColor(ContextCompat.getColor(this, R.color.gray_light))
            btnBack.setColorFilter(ContextCompat.getColor(this, R.color.white))
            btnTheme.setColorFilter(ContextCompat.getColor(this, R.color.white))
        } else {
            rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_light))
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
            tvPageInfo.setTextColor(ContextCompat.getColor(this, R.color.gray_medium))
            tvContent.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
            tvEndChapter.setTextColor(ContextCompat.getColor(this, R.color.gray_medium))
            btnBack.setColorFilter(ContextCompat.getColor(this, R.color.primary))
            btnTheme.setColorFilter(ContextCompat.getColor(this, R.color.primary))
        }
    }
}
