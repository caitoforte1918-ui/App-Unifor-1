package com.lithub.app.student

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R
import com.lithub.app.adapter.ReviewListAdapter
import com.lithub.app.dataclass.Review

class BookReviewsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ReviewListAdapter
    private val reviews = mutableListOf<Review>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_reviews)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val txtTituloLivro = findViewById<TextView>(R.id.txtTituloLivro)
        val txtAutorLivro = findViewById<TextView>(R.id.txtAutorLivro)
        val tituloLivro = intent.getStringExtra("tituloLivro")

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa o adapter com a lista vazia
        adapter = ReviewListAdapter(reviews)
        recyclerView.adapter = adapter

        if (tituloLivro != null) {
            // Primeiro buscamos o livro para pegar o ID/Referência dele
            db.collection("livros")
                .whereEqualTo("titulo", tituloLivro)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val bookDoc = documents.documents[0]
                        txtTituloLivro.text = bookDoc.getString("titulo")
                        txtAutorLivro.text = bookDoc.getString("autor")

                        // Configura o listener em tempo real para as avaliações deste livro
                        setupReviewsListener(bookDoc.id)
                    }
                }
        }

        findViewById<Button>(R.id.btnWriteReview).setOnClickListener {
            showWriteReviewDialog(tituloLivro)
        }
    }

    private fun setupReviewsListener(bookId: String) {
        db.collection("livros").document(bookId).collection("avaliacoes")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Erro ao carregar avaliações em tempo real", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    reviews.clear() // Limpa a lista atual para não duplicar
                    for (doc in snapshots) {
                        val review = Review(
                            image = R.color.primary_soft,
                            name = doc.getString("nome_estudante") ?: "Anônimo",
                            comment = doc.getString("comentario") ?: "",
                            score = doc.getDouble("nota") ?: 0.0,
                            book_name = "",
                            matricula = doc.getString("matricula") ?: ""
                        )
                        reviews.add(review)
                    }
                    adapter.notifyDataSetChanged() // Notifica o adapter que os dados mudaram
                }
            }
    }

    private fun showWriteReviewDialog(tituloLivro: String?) {
        val inputComentario = EditText(this).apply {
            hint = "Escreva sua avaliação aqui..."
        }

        val inputNota = EditText(this).apply {
            hint = "Nota (1 a 5)"
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(64, 32, 64, 0)
            addView(inputComentario)
            addView(inputNota)
        }

        AlertDialog.Builder(this)
            .setTitle("Escrever Avaliação")
            .setView(layout)
            .setPositiveButton("Enviar") { _, _ ->
                val comentario = inputComentario.text.toString().trim()
                val notaStr = inputNota.text.toString().trim()
                val nota = notaStr.toDoubleOrNull() ?: 0.0

                if (comentario.isNotEmpty() && nota in 1.0..5.0) {
                    val uid = auth.currentUser?.uid
                    if (uid != null && tituloLivro != null) {
                        db.collection("estudantes").document(uid).get()
                            .addOnSuccessListener { studentDoc ->
                                val nomeEstudante = studentDoc.getString("nome") ?: "Estudante"
                                val matricula = studentDoc.getString("matricula") ?: ""

                                db.collection("livros")
                                    .whereEqualTo("titulo", tituloLivro)
                                    .get()
                                    .addOnSuccessListener { documents ->
                                        if (!documents.isEmpty) {
                                            val bookRef = documents.documents[0].reference
                                            val reviewData = hashMapOf(
                                                "nome_estudante" to nomeEstudante,
                                                "matricula" to matricula,
                                                "comentario" to comentario,
                                                "nota" to nota
                                            )

                                            // Salva uma nova avaliação (gera ID automático para permitir múltiplas avaliações)
                                            bookRef.collection("avaliacoes")
                                                .add(reviewData)
                                                .addOnSuccessListener {
                                                    Toast.makeText(this, "Avaliação enviada com sucesso!", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    }
                            }
                    }
                } else {
                    Toast.makeText(this, "Por favor, preencha o comentário e coloque uma nota entre 1 e 5", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}