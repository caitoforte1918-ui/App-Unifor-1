package com.lithub.app.student

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R
import com.lithub.app.adapter.NotificationAdapter
import com.lithub.app.dataclass.Notification

class NotificationsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchNotifications(recyclerView)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_notifications
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> startActivity(Intent(this, HomeActivity::class.java))
                R.id.nav_books -> startActivity(Intent(this, MyBooksActivity::class.java))
                R.id.nav_chat -> startActivity(Intent(this, ChatbotActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            true
        }
    }

    private fun fetchNotifications(recyclerView: RecyclerView) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser?.uid

        if (uid != null) {
            db.collection("estudantes").document(uid).collection("notificacoes")
                .get()
                .addOnSuccessListener { documents ->
                    val notificationList = mutableListOf<Notification>()

                    if (documents.isEmpty) {
                        Toast.makeText(this,
                            "Nenhuma notificação encontrada",
                            Toast.LENGTH_SHORT)
                            .show()
                    }

                    for (document in documents) {
                        val title = document.getString("topico") ?: ""
                        val message = document.getString("descricao") ?: ""
                        val timestamp = document.getString("data_e_hora") ?: ""
                        notificationList.add(Notification(title, message, timestamp))
                    }
                    recyclerView.adapter = NotificationAdapter(notificationList)
                }
                .addOnFailureListener { exception ->
                    Log.e("NotificationsActivity", "Error getting notifications: ", exception)
                }
        }
    }
}