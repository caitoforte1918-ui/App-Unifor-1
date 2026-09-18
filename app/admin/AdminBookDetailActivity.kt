package com.lithub.app.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.lithub.app.R

class AdminBookDetailActivity : AppCompatActivity() {

    private lateinit var txtTitulo: TextView
    private lateinit var txtAutor: TextView
    private lateinit var txtSinopse: TextView
    private lateinit var txtAvaliacao: TextView

    private lateinit var statusLivro: String

    private lateinit var generoLivro: String

    private lateinit var db: FirebaseFirestore

    private var documentId: String? = null
    private var livroListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_book_detail)

        inicializarViews()

        db = FirebaseFirestore.getInstance()

        // Dados vindos do AdminReadBooksActivity
        val tituloRead = intent.getStringExtra("tituloAdapter")?.trim()
        val autorRead = intent.getStringExtra("autorAdapter")?.trim()

        // Dados vindos do AdminBookCollectionActivity
        val tituloCollection = intent.getStringExtra("tituloAdminCardBookListAdapter")?.trim()
        val autorCollection =  intent.getStringExtra("autorAdminCardBookListAdapter")?.trim()

        // Decide qual consulta executar
        when {

            // Consulta vindo da AdminBookCollectionActivity
            !tituloCollection.isNullOrEmpty() && !autorCollection.isNullOrEmpty() -> {

                carregarLivroPorCollection(
                    tituloCollection,
                    autorCollection
                )
            }

            // Consulta vindo da AdminReadBooksActivity
            !tituloRead.isNullOrEmpty() &&
                    !autorRead.isNullOrEmpty() -> {

                carregarLivroPorReadBooks(
                    tituloRead,
                    autorRead
                )
            }

            else -> {

                Toast.makeText(
                    this,
                    "Dados do livro inválidos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        configurarBotoes()

        findViewById<Button>(R.id.removeBookbtn).setOnClickListener {
            removerLivro()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        livroListener?.remove()
    }

    private fun inicializarViews() {

        txtTitulo = findViewById(R.id.txtTitulo)
        txtAutor = findViewById(R.id.txtAutor)
        txtSinopse = findViewById(R.id.txtSinopse)
        txtAvaliacao = findViewById(R.id.txtAvaliacao)

        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }
    }

    // Consulta vinda da AdminBookCollectionActivity
    private fun carregarLivroPorCollection(titulo: String, autor: String) {

        consultarLivro(titulo, autor)
    }

    // Consulta vinda da AdminReadBooksActivity
    private fun carregarLivroPorReadBooks(titulo: String, autor: String) {

        consultarLivro(titulo, autor)
    }

    // Função genérica de consulta
    private fun consultarLivro(titulo: String, autor: String) {

        db.collection("livros")
            .whereEqualTo("titulo", titulo)
            .whereEqualTo("autor", autor)
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {

                    Toast.makeText(
                        this,
                        "Livro não encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@addOnSuccessListener
                }

                val document = documents.documents[0]
                documentId = document.id
                iniciarSnapshotListener(document.id)
            }
            .addOnFailureListener {
                mostrarErro()
            }
    }

    private fun iniciarSnapshotListener(id: String) {
        livroListener?.remove()
        livroListener = db.collection("livros").document(id)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    mostrarErro()
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    preencherDadosLivro(snapshot)
                }
            }
    }

    private fun removerLivro() {
        val id = documentId ?: run {
            Toast.makeText(this, "Aguarde o carregamento do livro", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("livros")
            .document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Livro removido com sucesso",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "Erro ao remover livro",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }



    // Preenche os TextViews
    private fun preencherDadosLivro(document: DocumentSnapshot) {

        val titulo = document.getString("titulo")
        val autor = document.getString("autor")
        val genero = document.getString("genero")
        val sinopse = document.getString("sinopse")
        val avaliacao = document.getString("avaliacao")
        val status = document.getString("status")

        txtTitulo.text = titulo
        txtAutor.text = autor
        txtSinopse.text = sinopse
        txtAvaliacao.text = "Avaliação: $avaliacao/5.0"

        if (genero != null) {
            generoLivro = genero
        }

        if (status != null) {
            statusLivro = status
        }

    }

    // Configuração dos botões
    private fun configurarBotoes() {


        findViewById<Button>(R.id.editBookbtn).setOnClickListener {

            val intent = Intent(
                this,
                AdminEditBookActivity::class.java
            )

            intent.putExtra("titulo", txtTitulo.text.toString().trim())
            intent.putExtra("autor", txtAutor.text.toString().trim())
            intent.putExtra("genero",generoLivro)
            intent.putExtra("sinopse", txtSinopse.text.toString().trim())
            intent.putExtra("avaliacao", txtAvaliacao.text.toString().trim())
            intent.putExtra("status",statusLivro)

            startActivity(intent)
        }


    }

    private fun mostrarErro() {

        Toast.makeText(
            this,
            "Falha ao ler dados do livro",
            Toast.LENGTH_SHORT
        ).show()
    }
}