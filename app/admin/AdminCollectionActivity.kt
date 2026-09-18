package com.lithub.app.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lithub.app.R
import kotlin.jvm.java

class AdminCollectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_collection)

        findViewById<Button>(R.id.booksCollectionbtn).setOnClickListener {
            startActivity(Intent(this, AdminBookCollectionActivity::class.java))
        }

        findViewById<Button>(R.id.gamesCollectionbtn).setOnClickListener {
            startActivity(Intent(this, AdminGameCollectionActivity::class.java))
        }


        findViewById<Button>(R.id.creatBookbtn).setOnClickListener {
            startActivity(Intent(this, AdminAddBookActivity::class.java))
        }

        findViewById<Button>(R.id.creatGamebtn).setOnClickListener {
            startActivity(Intent(this, AdminAddGameActivity::class.java))
        }

        val bottomAdmin = findViewById<BottomNavigationView>(R.id.bottomAdmin)
        bottomAdmin.selectedItemId = R.id.nav_admin_collection
        bottomAdmin.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_admin_home ->
                    startActivity(Intent(this, AdminHomeActivity::class.java))

                R.id.nav_admin_records ->
                    startActivity(Intent(this, AdminRecordsActivity::class.java))

                R.id.nav_admin_requests ->
                    startActivity(Intent(this, AdminRequestActivity::class.java))

            }
            true
        }
    }
}