package com.lithub.app.student

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.lithub.app.R
import com.lithub.app.LoginActivity

class ProfileActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Agora o UID é buscado dinamicamente para evitar ser nulo se o login demorar
    private val currentUid: String?
        get() = auth.currentUser?.uid

    // UI Elements
    private lateinit var usernameTv: TextView
    private lateinit var nicknameTv: TextView
    private lateinit var matriculationTv: TextView
    private lateinit var descriptionTv: TextView
    private lateinit var profileIv: ImageView
    private lateinit var btnEditNickname: ImageView
    private lateinit var btnEditDesc: ImageView
    private lateinit var btnPhotoAction: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initializeViews()
        setupListeners()
        loadLocalData() // Carrega o que estiver salvo no celular primeiro
        loadUserData()  // Tenta carregar do Firebase depois
        setupBottomNavigation()
    }

    override fun onStart() {
        super.onStart()
        loadLocalData() // Atualiza os dados sempre que a tela voltar a ser visível (ex: após mudar foto)
    }

    private fun initializeViews() {
        usernameTv = findViewById(R.id.username)
        nicknameTv = findViewById(R.id.nickname)
        matriculationTv = findViewById(R.id.matriculation)
        descriptionTv = findViewById(R.id.desc)
        profileIv = findViewById(R.id.btnPhoto)
        btnEditNickname = findViewById(R.id.btnEditNickname)
        btnEditDesc = findViewById(R.id.btnEditDesc)
        btnPhotoAction = findViewById(R.id.btnOpenPhoto)
        btnLogout = findViewById(R.id.btnLogout)
    }

    private fun setupListeners() {
        // Logout
        btnLogout.setOnClickListener {
            val uid = currentUid
            if (uid != null) {
                db.collection("estudantes").document(uid)
                    .update("status", 0)
                    .addOnCompleteListener {
                        auth.signOut()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
            } else {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        //Edita o apelido - clicando em ambos o ícone e o texto atual
        btnEditNickname.setOnClickListener { openEditProfileDialog("Apelido", "apelido", nicknameTv.text.toString()) }
        nicknameTv.setOnClickListener { openEditProfileDialog("Apelido", "apelido", nicknameTv.text.toString()) }

        //Edita a descrição - clicando em ambos o ícone e o texto atual
        btnEditDesc.setOnClickListener { openEditProfileDialog("Descrição", "descricao", descriptionTv.text.toString()) }
        descriptionTv.setOnClickListener { openEditProfileDialog("Descrição", "descricao", descriptionTv.text.toString()) }

        //Edita a foto - clicando no botão ou na imagem de perfil
        btnPhotoAction.setOnClickListener {
            startActivity(Intent(this, ProfilePhotoActivity::class.java))
        }
        profileIv.setOnClickListener {
            startActivity(Intent(this, ProfilePhotoActivity::class.java))
        }
    }

    // Carrega os dados locais se estiverem disponíveis
    private fun loadLocalData() {
        val sharedPref = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val savedNickname = sharedPref.getString("apelido", null)
        val savedNome = sharedPref.getString("nome", null)
        val savedDesc = sharedPref.getString("descricao", null)
        val savedPhoto = sharedPref.getString("foto", null)

        // Prioridade para o nome no topo: Apelido > Nome Real > Padrão
        if (!savedNickname.isNullOrEmpty()) {
            usernameTv.text = savedNickname
            nicknameTv.text = savedNickname
        } else if (!savedNome.isNullOrEmpty()) {
            usernameTv.text = savedNome
        }

        if (savedDesc != null) descriptionTv.text = savedDesc

        if (savedPhoto != null && !isDestroyed) {
            Glide.with(this)
                .load(savedPhoto)
                .circleCrop()
                .placeholder(R.drawable.unifor_logo)
                .into(profileIv)
        }
    }

    // Carrega os dados do Firestore se estiverem disponíveis
    private fun loadUserData() {
        val uid = currentUid
        if (uid == null) {
            Log.e("ProfileActivity", "Usuário em Modo de Teste. Mantendo dados locais.")
            return
        }

        db.collection("estudantes").document(uid)
            .addSnapshotListener { document, error ->
                if (error != null) {
                    Log.e("ProfileActivity", "Error listening to profile updates", error)
                    return@addSnapshotListener
                }

                if (document != null && document.exists()) {
                    val sharedPref = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()

                    // Sincroniza os dados do Firestore com a UI e Cache
                    val nomeRemoto = document.getString("nome")
                    val apelidoRemoto = document.getString("apelido")
                    val matriculaRemota = document.getString("matricula")
                    val descRemota = document.getString("descricao")
                    val fotoUrl = document.getString("foto")

                    // Salva tudo localmente para garantir persistência offline
                    nomeRemoto?.let { editor.putString("nome", it) }
                    apelidoRemoto?.let { editor.putString("apelido", it) }
                    descRemota?.let { editor.putString("descricao", it) }
                    fotoUrl?.let { editor.putString("foto", it) }
                    editor.apply()

                    // Atualiza UI
                    if (!apelidoRemoto.isNullOrEmpty()) {
                        usernameTv.text = apelidoRemoto
                        nicknameTv.text = apelidoRemoto
                    } else if (!nomeRemoto.isNullOrEmpty()) {
                        usernameTv.text = nomeRemoto
                    }

                    if (!matriculaRemota.isNullOrEmpty()) matriculationTv.text = matriculaRemota
                    if (!descRemota.isNullOrEmpty()) descriptionTv.text = descRemota

                    if (!isDestroyed && !fotoUrl.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(fotoUrl)
                            .circleCrop()
                            .placeholder(R.drawable.unifor_logo)
                            .into(profileIv)
                    }
                }
            }
    }


    // Abre um diálogo para editar um campo
    private fun openEditProfileDialog(label: String, firestoreField: String, currentVal: String) {
        val context = this
        val input = EditText(context).apply {
            setText(currentVal)
            setSelection(text.length)
            requestFocus()
        }

        // Configura o diálogo
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(64, 32, 64, 0)
            addView(input)
        }

        // Cria e exibe o diálogo
        val dialog = AlertDialog.Builder(context)
            .setTitle("Editar $label")
            .setView(layout)
            .setPositiveButton("Salvar") { _, _ ->
                val newValue = input.text.toString().trim()
                updateFirestoreField(firestoreField, newValue, label)
            }
            .setNegativeButton("Cancelar", null)
            .create()

        // Abre o teclado virtual
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    private fun updateFirestoreField(field: String, value: String, label: String) {
        // 1. Salva Localmente (Garante que "fique salvo" imediatamente no aparelho)
        val sharedPref = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(field, value)
            apply()
        }

        // 2. Atualiza a interface
        updateLocalUI(field, value)

        val uid = currentUid
        if (uid == null) {
            Log.w("ProfileActivity", "Modo de Teste: Alteração persistida localmente.")
            Toast.makeText(this, "$label salvo no aparelho!", Toast.LENGTH_SHORT).show()
            return
        }

        // 3. Salva no Firebase (Persistência na Nuvem para usuários reais)
        val data = mapOf(field to value)
        db.collection("estudantes").document(uid)
            .set(data, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("ProfileActivity", "Sucesso ao sincronizar $field com a nuvem")
                Toast.makeText(this, "$label sincronizado com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("ProfileActivity", "Erro ao sincronizar com Firestore", e)
            }
    }

    // Atualiza a UI localmente com o novo valor
    private fun updateLocalUI(field: String, value: String) {
        when (field) {
            "apelido" -> {
                nicknameTv.text = value
                usernameTv.text = value // Atualiza o nome de destaque no topo
                Log.d("ProfileActivity", "UI Local: Apelido e Topo alterados para $value")
            }
            "descricao" -> {
                descriptionTv.text = value
                Log.d("ProfileActivity", "UI Local: Descrição alterada")
            }
        }
    }

    // Configura a Barra de Navegação
    private fun setupBottomNavigation() {
        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottom.selectedItemId = R.id.nav_profile
        bottom.setOnItemSelectedListener {
            when(it.itemId){
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
            }
            true
        }
    }
}
