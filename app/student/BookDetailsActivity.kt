package com.lithub.app.student

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import java.util.Locale
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R

class BookDetailsActivity : AppCompatActivity() {

    private lateinit var txtTituloDetalhes: TextView
    private lateinit var txtAutorDetalhes: TextView
    private lateinit var txtSinopseDetalhes: TextView
    private lateinit var txtNotaDetalhes: TextView

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        txtTituloDetalhes = findViewById(R.id.txtTituloDetalhes)
        txtAutorDetalhes = findViewById(R.id.txtAutorDetalhes)
        txtSinopseDetalhes = findViewById(R.id.txtSinopseDetalhes)
        txtNotaDetalhes = findViewById(R.id.txtNotaDetalhes)

        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        // recebe o título enviado pelo adapter
        val tituloLivro = intent.getStringExtra("tituloLivro")

        // consulta dinâmica
        if (!tituloLivro.isNullOrEmpty()) {

            buscarLivroPorTitulo(tituloLivro)
            verificarSeEFavorito(tituloLivro)
            calcularMediaLivro(tituloLivro)
            verificarSeFoiLido(tituloLivro)

        } else {

            Toast.makeText(
                this,
                "Livro não encontrado",
                Toast.LENGTH_SHORT
            ).show()

        }

        configurarFavoritos()
        configurarBotoes()
    }

    private fun buscarLivroPorTitulo(titulo: String) {

        db.collection("livros")
            .whereEqualTo("titulo", titulo)
            .get()
            .addOnSuccessListener { documentos ->

                for (documento in documentos) {

                    txtTituloDetalhes.text = documento.getString("titulo")
                    txtAutorDetalhes.text = documento.getString("autor")
                    txtSinopseDetalhes.text = documento.getString("sinopse")
                }
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Erro ao buscar livro",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun verificarSeEFavorito(titulo: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val btnFavorite = findViewById<Button>(R.id.btnFavorite)

        db.collection("estudantes")
            .document(uid)
            .collection("livros_favoritos")
            .whereEqualTo("titulo", titulo)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    btnFavorite.text = "Desfavoritar Livro"
                    btnFavorite.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#BD4A47"))
                } else {
                    btnFavorite.text = "Favoritar Livro"
                    btnFavorite.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#309940"))
                }
            }
    }

    private fun configurarFavoritos() {

        findViewById<Button>(R.id.btnFavorite).setOnClickListener {

            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
            val titulo = txtTituloDetalhes.text.toString().trim()

            db.collection("estudantes")
                .document(uid)
                .collection("livros_favoritos")
                .whereEqualTo("titulo", titulo)
                .get()

                .addOnSuccessListener { documents ->

                    if (documents.isEmpty) {

                        val infos = hashMapOf(
                            "titulo" to titulo,
                            "titulo_lower" to titulo.lowercase(),
                            "autor" to txtAutorDetalhes.text.toString().trim(),
                            "sinopse" to txtSinopseDetalhes.text.toString().trim()
                        )

                        db.collection("estudantes")
                            .document(uid)
                            .collection("livros_favoritos")
                            .add(infos)

                            .addOnSuccessListener {
                                Toast.makeText(this, "Livro salvo nos favoritos", Toast.LENGTH_SHORT).show()
                                verificarSeEFavorito(titulo)
                            }

                    } else {
                        for (document in documents) {
                            document.reference.delete()
                        }
                        Toast.makeText(this, "Livro removido dos favoritos", Toast.LENGTH_SHORT).show()
                        verificarSeEFavorito(titulo)
                    }
                }
        }
    }

    private fun calcularMediaLivro(titulo: String) {
        db.collection("livros")
            .whereEqualTo("titulo", titulo)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.collection("avaliacoes")
                        .get()
                        .addOnSuccessListener { avaliacoes ->
                            var somaNotas = 0.0
                            var totalAvaliacoes = 0

                            for (avaliacao in avaliacoes) {
                                val nota = avaliacao.getDouble("nota")
                                if (nota != null) {
                                    somaNotas += nota
                                    totalAvaliacoes++
                                }
                            }

                            if (totalAvaliacoes > 0) {
                                val media = somaNotas / totalAvaliacoes
                                txtNotaDetalhes.text = String.format(Locale.US, "%.1f / 5", media)
                            } else {
                                txtNotaDetalhes.text = "0.0 / 5"
                            }
                        }
                }
            }
    }

    private fun configurarBotoes() {

        findViewById<Button>(R.id.btnReviews).setOnClickListener {

            val intent = Intent(this, BookReviewsActivity::class.java)
            intent.putExtra("tituloLivro", txtTituloDetalhes.text.toString())
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnRead).setOnClickListener {
            val intent = Intent(this, BookChaptersActivity::class.java)
            intent.putExtra("tituloLivro", txtTituloDetalhes.text.toString())
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnSimilar).setOnClickListener {
            val intent = Intent(this, BookSimilarActivity::class.java)
            intent.putExtra("tituloLivro", txtTituloDetalhes.text.toString())
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnBookRead).setOnClickListener {
            buscarMatriculaEAlternarStatus()
        }
    }

    private fun verificarSeFoiLido(titulo: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        db.collection("estudantes").document(uid).get().addOnSuccessListener { document ->
            val matricula = document.getString("matricula")
            if (matricula != null) {
                verificarStatusLeitura(matricula, titulo)
            }
        }
    }

    private fun verificarStatusLeitura(matricula: String, titulo: String) {
        db.collection("visualizacoes")
            .whereEqualTo("matricula_estudante", matricula)
            .whereEqualTo("titulo", titulo)
            .get()
            .addOnSuccessListener { documents ->
                val btnBookRead = findViewById<Button>(R.id.btnBookRead)
                if (!documents.isEmpty) {
                    btnBookRead.text = "Lido"
                } else {
                    btnBookRead.text = "Marcar como Lido"
                }
            }
    }

    private fun buscarMatriculaEAlternarStatus() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val titulo = txtTituloDetalhes.text.toString().trim()
        val autor = txtAutorDetalhes.text.toString().trim()

        db.collection("estudantes").document(uid).get().addOnSuccessListener { document ->
            val matricula = document.getString("matricula")
            if (matricula != null) {
                alternarStatusLeitura(matricula, titulo, autor)
            }
        }
    }

    private fun alternarStatusLeitura(matricula: String, titulo: String, autor: String) {
        val btnBookRead = findViewById<Button>(R.id.btnBookRead)
        db.collection("visualizacoes")
            .whereEqualTo("matricula_estudante", matricula)
            .whereEqualTo("titulo", titulo)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    val dados = hashMapOf(
                        "titulo" to titulo,
                        "autor" to autor,
                        "matricula_estudante" to matricula
                    )
                    db.collection("visualizacoes").add(dados).addOnSuccessListener {
                        btnBookRead.text = "Lido"
                        Toast.makeText(this, "Livro marcado como lido", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    for (doc in documents) {
                        doc.reference.delete()
                    }
                    btnBookRead.text = "Marcar como Lido"
                    Toast.makeText(this, "Livro removido dos lidos", Toast.LENGTH_SHORT).show()
                }
            }
    }
}