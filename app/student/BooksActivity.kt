package com.lithub.app.student

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.lithub.app.R
import com.lithub.app.adapter.CardBookListAdapter
import com.lithub.app.dataclass.CardBook

class BooksActivity : AppCompatActivity() {

    private lateinit var inputSearchBooks: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CardBookListAdapter
    private lateinit var db: FirebaseFirestore

    private val cardBooks = mutableListOf<CardBook>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_books)



        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        inputSearchBooks = findViewById(R.id.input_search_books)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CardBookListAdapter(cardBooks)
        recyclerView.adapter = adapter

        db = FirebaseFirestore.getInstance()

        // Carrega todos os livros
        carregarLivros()

        inputSearchBooks.setOnEditorActionListener { _, actionId, _ ->

            // Usuário clicou no botão de pesquisar
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val texto = inputSearchBooks.text.toString()
                    .lowercase()
                    .trim()

                // Pesquisa apenas se o campo não estiver vazio
                if (texto.isNotEmpty()) {
                    pesquisarLivros(texto)
                } else {
                    carregarLivros()
                }

                true
            } else {
                false
            }
        }
    }

    // Função responsável por carregar todos os livros
    private fun carregarLivros() {

        db.collection("livros")
            .get()
            .addOnSuccessListener { documentos ->

                atualizarLista(documentos)

            }
            .addOnFailureListener {

                mostrarErro()
            }
    }

    // Função responsável pela pesquisa de livros
    private fun pesquisarLivros(texto: String) {

        db.collection("livros")
            .orderBy("titulo_lower")
            .startAt(texto)
            .endAt(texto + "\uf8ff")
            .get()
            .addOnSuccessListener { documentos ->

                atualizarLista(documentos)

            }
            .addOnFailureListener {

                mostrarErro()
            }
    }

    // Atualiza a lista do RecyclerView
    private fun atualizarLista(documentos: QuerySnapshot) {

        cardBooks.clear()

        for (documento in documentos) {

            val title = documento.getString("titulo").toString()
            val autor = documento.getString("autor").toString()
            val sinopse = documento.getString("sinopse").toString()
            val status = documento.getString("status").toString()

            if (status != "Bloqueado") {

                cardBooks.add(
                    CardBook(
                        title,
                        autor,
                        sinopse,
                        R.color.primary_soft,
                        "",
                        0
                    )
                )

            }

        }

        adapter.notifyDataSetChanged()
    }

    // Exibe mensagem de erro
    private fun mostrarErro() {

        Toast.makeText(
            this,
            "Falha ao conectar com banco de dados",
            Toast.LENGTH_SHORT
        ).show()
    }
}