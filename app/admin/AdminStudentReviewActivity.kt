package com.lithub.app.admin

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R
import com.lithub.app.adapter.ReviewListAdapter
import com.lithub.app.dataclass.Review

class AdminStudentReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_review)

        val nomeEstudante = intent.getStringExtra("nome") ?: ""

        val reviews = mutableListOf<Review>()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewReview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ReviewListAdapter(reviews)
        recyclerView.adapter = adapter

        val db = FirebaseFirestore.getInstance()

        // O Firestore utiliza collectionGroup para buscar em todas as subcoleções com o mesmo nome
        // (avaliacoes) dentro de qualquer documento da coleção pai (livros).
        db.collectionGroup("avaliacoes")
            .whereEqualTo("nome_estudante", nomeEstudante)
            .addSnapshotListener { querySnapshot, error ->

                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }

                reviews.clear()

                querySnapshot?.forEach { doc ->

                    val nome = doc.getString("nome_estudante") ?: ""
                    val comentario = doc.getString("comentario") ?: ""
                    val nota = doc.getDouble("nota") ?: 0.0

                    // Busca o título do livro pai
                    doc.reference.parent.parent?.get()?.addOnSuccessListener { bookDoc ->
                        val tituloLivro = bookDoc.getString("titulo") ?: "Livro"

                        reviews.add(
                            Review(
                                image = R.color.primary_soft,
                                name = nome,
                                book_name = tituloLivro,
                                comment = comentario,
                                score = nota
                            )
                        )
                        adapter.notifyDataSetChanged()
                    }
                }
            }

    }
}
