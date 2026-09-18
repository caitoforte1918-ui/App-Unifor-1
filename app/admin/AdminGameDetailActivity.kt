package com.lithub.app.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lithub.app.R

class AdminGameDetailActivity : AppCompatActivity() {

    private lateinit var statusJogo: String
    private lateinit var regrasJogo: String
    private lateinit var db: FirebaseFirestore
    private var documentId: String? = null
    private var gameListener: ListenerRegistration? = null

    private lateinit var txtGameTitle: TextView
    private lateinit var txtGameDesc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_game_detail)

        txtGameTitle = findViewById(R.id.txtGameTitle)
        txtGameDesc = findViewById(R.id.txtGameDesc)

        val tituloAdminCardGameListAdapter = intent.getStringExtra("tituloAdminCardGameListAdapter")


        //Botão de voltar
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }


        db = FirebaseFirestore.getInstance()

        consultarJogo(tituloAdminCardGameListAdapter)

        //Botão de editar jogo
        findViewById<Button>(R.id.editGamebtn).setOnClickListener {
            val intent = Intent(this, AdminEditGameActivity::class.java)
            intent.putExtra("titulo", txtGameTitle.text.toString())
            intent.putExtra("descricao", txtGameDesc.text.toString())
            intent.putExtra("regras", if (::regrasJogo.isInitialized) regrasJogo else "")
            intent.putExtra("status", if (::statusJogo.isInitialized) statusJogo else "")
            startActivity(intent)
        }

        //Botão de remover jogo
        findViewById<Button>(R.id.removeGamebtn).setOnClickListener {
            removerJogo()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameListener?.remove()
    }

    private fun consultarJogo(titulo: String?) {
        if (titulo == null) return

        db.collection("jogos")
            .whereEqualTo("titulo", titulo.trim())
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    documentId = document.id
                    iniciarSnapshotListener(document.id)
                }
            }
    }

    private fun iniciarSnapshotListener(id: String) {
        gameListener?.remove()
        gameListener = db.collection("jogos").document(id)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                if (snapshot != null && snapshot.exists()) {
                    val titulo = snapshot.getString("titulo")
                    val desc = snapshot.getString("descricao")
                    val regras = snapshot.getString("regras")
                    val status = snapshot.getString("status")

                    txtGameTitle.text = titulo
                    txtGameDesc.text = desc

                    if (regras != null) regrasJogo = regras
                    if (status != null) statusJogo = status
                }
            }
    }

    private fun removerJogo() {
        val id = documentId ?: return

        db.collection("jogos")
            .document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Jogo removido com sucesso", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao remover Jogo", Toast.LENGTH_SHORT).show()
            }
    }
}
