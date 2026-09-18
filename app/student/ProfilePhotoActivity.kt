package com.lithub.app.student

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.lithub.app.R

class ProfilePhotoActivity : AppCompatActivity() {

    private lateinit var imgProfileBig: ImageView
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val currentUid: String?
        get() = auth.currentUser?.uid

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadImageToFirebase(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_photo)

        imgProfileBig = findViewById(R.id.imgProfileBig)
        val btnChangePhoto = findViewById<Button>(R.id.btnChangePhoto)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        loadCurrentPhoto()

        btnChangePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        btnBack.setOnClickListener {
            finish()
        }

        setupBottomNavigation()
    }

    private fun loadCurrentPhoto() {
        val sharedPref = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val localFoto = sharedPref.getString("foto", null)

        // Tenta carregar do cache local primeiro para agilidade
        if (!localFoto.isNullOrEmpty()) {
            Glide.with(this)
                .load(localFoto)
                .circleCrop()
                .placeholder(R.drawable.unifor_logo)
                .into(imgProfileBig)
        }

        val uid = currentUid ?: return

        db.collection("estudantes").document(uid)
            .addSnapshotListener { document, _ ->
                if (document != null && document.exists()) {
                    val fotoUrl = document.getString("foto")
                    if (!fotoUrl.isNullOrEmpty() && !isDestroyed && fotoUrl != localFoto) {
                        Glide.with(this)
                            .load(fotoUrl)
                            .circleCrop()
                            .placeholder(R.drawable.unifor_logo)
                            .into(imgProfileBig)

                        savePhotoLocally(fotoUrl)
                    }
                }
            }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val uid = currentUid

        // Feedback imediato: atualiza a imagem assim que o usuário seleciona
        Glide.with(this)
            .load(fileUri)
            .circleCrop()
            .into(imgProfileBig)

        // Salva localmente para persistência
        savePhotoLocally(fileUri.toString())

        if (uid == null) {
            Toast.makeText(this, "Foto alterada localmente (Modo de Teste)", Toast.LENGTH_LONG).show()
            return
        }

        val storageRef = storage.reference.child("perfil/$uid.jpg")
        Toast.makeText(this, "Fazendo upload da foto...", Toast.LENGTH_SHORT).show()

        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri: Uri ->
                    updateFirestoreProfile(downloadUri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("Upload", "Erro: ${e.message}")
                Toast.makeText(this, "Erro ao enviar foto para o servidor", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateFirestoreProfile(url: String) {
        val uid = currentUid ?: return
        savePhotoLocally(url) // Atualiza o cache local com a URL final

        db.collection("estudantes").document(uid)
            .update("foto", url)
            .addOnSuccessListener {
                Toast.makeText(this, "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar link da foto", Toast.LENGTH_SHORT).show()
            }
    }

    private fun savePhotoLocally(path: String) {
        val sharedPref = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("foto", path)
            apply()
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_profile
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }
                R.id.nav_books -> {
                    startActivity(Intent(this, MyBooksActivity::class.java))
                    finish()
                }
                R.id.nav_chat -> {
                    startActivity(Intent(this, ChatbotActivity::class.java))
                    finish()
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    finish()
                }
                R.id.nav_profile -> finish()
            }
            true
        }
    }
}
