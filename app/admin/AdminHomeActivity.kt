package com.lithub.app.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R

class AdminHomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        val btnTotalStudents = findViewById<Button>(R.id.btnTotalStudents)
        val btnOnlineStudents = findViewById<Button>(R.id.btnOnlineStudents)
        
        btnTotalStudents.setOnClickListener { startActivity(
            Intent(
                this,
                AdminStudentsActivity::class.java
            )
        ) }

        val db = FirebaseFirestore.getInstance()
        db.collection("estudantes").get().addOnSuccessListener { result ->
            val numAlunos = result.size()
            btnTotalStudents.text = "Total de Alunos\n$numAlunos"

            val numAtivos = result.documents.count { it.getLong("status")?.toInt() == 1 }
            btnOnlineStudents.text = "Alunos Online \n $numAtivos"
        }

        val bottomAdmin = findViewById<BottomNavigationView>(R.id.bottomAdmin)
        bottomAdmin.selectedItemId = R.id.nav_admin_home
        bottomAdmin.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_admin_records -> startActivity(Intent(this, AdminRecordsActivity::class.java))
                R.id.nav_admin_collection -> startActivity(Intent(this, AdminCollectionActivity::class.java))
                R.id.nav_admin_requests -> startActivity(Intent(this, AdminRequestActivity::class.java))
            }
            true
        }

    }
}