package com.lithub.app.student

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R
import com.lithub.app.adapter.ChapterAdapter
import com.lithub.app.dataclass.Chapter

class BookChaptersActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var rvChapters: RecyclerView
    private var tituloLivro: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_chapters)

        //Recebe o título do livro da Activity anterior
        tituloLivro = intent.getStringExtra("tituloLivro")

        //Configuração do título do livro
        val tvBookTitle = findViewById<TextView>(R.id.tvBookTitle)
        tvBookTitle.text = tituloLivro ?: "Livro Desconhecido"

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        //Configuração do RecyclerView
        rvChapters = findViewById(R.id.rvChapters)
        rvChapters.layoutManager = LinearLayoutManager(this)

        //Verifica se o título do livro foi passado corretamente
        tituloLivro?.let {
            fetchChaptersFromFirestore(it)
        } ?: run {
            Toast.makeText(this, "Título do livro não informado", Toast.LENGTH_SHORT).show()
        }
    }

    //Função para puxar os dados do livro do Firestore
    private fun fetchChaptersFromFirestore(titulo: String) {
        db.collection("livros")
            .whereEqualTo("titulo", titulo)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val bookDoc = documents.documents[0]
                    fetchSubcollectionChapters(bookDoc.id)
                } else {
                    Toast.makeText(this, "Livro ainda não possui capítulos", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar livro", Toast.LENGTH_SHORT).show()
            }
    }

    //Função para puxar os dados da subcoleção de Capítulos
    private fun fetchSubcollectionChapters(bookId: String) {
        db.collection("livros").document(bookId).collection("capitulos")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val chapterList = mutableListOf<Chapter>()
                    for (doc in documents) {
                        val title = doc.getString("capitulo") ?: "Capítulo sem título"
                        val pages = doc.getString("paginas") ?: "Sem páginas"
                        chapterList.add(Chapter(title, pages))
                    }
                    updateRecyclerView(chapterList)
                } else {
                    Toast.makeText(this, "Livro ainda não possui capítulos", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar capítulos", Toast.LENGTH_SHORT).show()
            }
    }

    //Função para atualizar o RecyclerView com os dados dos capítulos
    private fun updateRecyclerView(chapters: List<Chapter>) {
        val adapter = ChapterAdapter(chapters) { chapter ->
            val intent = Intent(this, BookReadingActivity::class.java)
            intent.putExtra("CHAPTER_TITLE", chapter.title)
            startActivity(intent)
        }
        rvChapters.adapter = adapter
    }
}
