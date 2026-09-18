package com.lithub.app.admin

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.lithub.app.R
import com.lithub.app.adapter.AdminCardBookListAdapter
import com.lithub.app.dataclass.CardBook

class AdminBookCollectionActivity : AppCompatActivity() {

    private lateinit var inputSearchBooks: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminCardBookListAdapter
    private lateinit var db: FirebaseFirestore
    private var registration: ListenerRegistration? = null

    private val cardBooks = mutableListOf<CardBook>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_book_collection)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }


        inputSearchBooks = findViewById(R.id.input_search_books)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdminCardBookListAdapter(cardBooks)
        recyclerView.adapter = adapter

        db = FirebaseFirestore.getInstance()

        // Carrega todos os livros
        carregarLivros()

        inputSearchBooks.setOnEditorActionListener { _, actionId, _ ->

            // Usuário clicou no botão de pesquisar
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val texto = inputSearchBooks.text.toString().lowercase().trim()

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

    override fun onDestroy() {
        super.onDestroy()
        registration?.remove()
    }

    // Função responsável por carregar todos os livros
    private fun carregarLivros() {
        registration?.remove()
        registration = db.collection("livros")
            .addSnapshotListener { documentos, e ->
                if (e != null) {
                    mostrarErro()
                    return@addSnapshotListener
                }

                if (documentos != null) {
                    atualizarLista(documentos)
                }
            }
    }

    // Função responsável pela pesquisa de livros
    private fun pesquisarLivros(texto: String) {
        registration?.remove()
        registration = db.collection("livros")
            .orderBy("titulo_lower")
            .startAt(texto)
            .endAt(texto + "\uf8ff")
            .addSnapshotListener { documentos, e ->
                if (e != null) {
                    mostrarErro()
                    return@addSnapshotListener
                }

                if (documentos != null) {
                    atualizarLista(documentos)
                }
            }
    }

    // Atualiza a lista do RecyclerView
    private fun atualizarLista(documentos: QuerySnapshot) {

        cardBooks.clear()

        for (documento in documentos) {

            val title = documento.getString("titulo").toString()
            val autor = documento.getString("autor").toString()
            val sinopse = documento.getString("sinopse").toString()

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