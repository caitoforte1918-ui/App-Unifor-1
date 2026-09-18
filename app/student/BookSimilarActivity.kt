package com.lithub.app.student

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R
import com.lithub.app.adapter.SimilarBookListAdapter
import com.lithub.app.dataclass.Book
import com.lithub.app.dataclass.ImageBook

class BookSimilarActivity : AppCompatActivity(){
    private lateinit var db: FirebaseFirestore
    private var books = mutableListOf<Book>()
    private lateinit var adapter: SimilarBookListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_similar)

        val tituloLivro = intent.getStringExtra("tituloLivro")

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        
        db = FirebaseFirestore.getInstance()

        adapter = SimilarBookListAdapter(books)
        recyclerView.adapter = adapter

        if (!tituloLivro.isNullOrEmpty()) {
            buscarLivrosSemelhantes(tituloLivro)
        } else {
            Toast.makeText(this, "Título do livro não informado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buscarLivrosSemelhantes(titulo: String) {
        db.collection("livros")
            .whereEqualTo("titulo", titulo)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "Livro não encontrado", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    document.reference.collection("semelhantes")
                        .get()
                        .addOnSuccessListener { similarDocs ->
                            books.clear()
                            for (similarDoc in similarDocs) {
                                val book = Book(
                                    genre = similarDoc.getString("genero") ?: "",
                                    image = R.color.primary_soft, // Usando cor padrão conforme padrão do projeto
                                    title = similarDoc.getString("titulo") ?: ""
                                )
                                books.add(book)
                            }
                            adapter.notifyDataSetChanged()
                            
                            if (books.isEmpty()) {
                                Toast.makeText(this, "Nenhum livro semelhante encontrado", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao carregar livros semelhantes", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar livro base", Toast.LENGTH_SHORT).show()
            }
    }
}
