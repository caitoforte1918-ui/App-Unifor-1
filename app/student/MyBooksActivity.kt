package com.lithub.app.student

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R
import com.lithub.app.adapter.MyBooksListAdapter
import com.lithub.app.dataclass.ImageBook

class MyBooksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var inputSearch: EditText

    private lateinit var adapter: MyBooksListAdapter

    private val imageBooks = mutableListOf<ImageBook>()

    private val db = FirebaseFirestore.getInstance()

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_books)

        inicializarViews()
        configurarRecyclerView()
        carregarLivrosFavoritos()
        configurarPesquisa()
        configurarBottomNavigation()
    }

    private fun inicializarViews() {

        recyclerView = findViewById(R.id.recyclerViewGames)
        inputSearch = findViewById(R.id.input_search_books)
    }

    private fun configurarRecyclerView() {

        recyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter = MyBooksListAdapter(this, imageBooks)

        recyclerView.adapter = adapter
    }

    private fun carregarLivrosFavoritos() {

        val uid = auth.currentUser?.uid

        if (uid == null) {

            Toast.makeText(
                this,
                "Usuário não autenticado",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        db.collection("estudantes")
            .document(uid)
            .collection("livros_favoritos")
            .get()

            .addOnSuccessListener { documents ->

                imageBooks.clear()

                if (documents.isEmpty) {

                    Toast.makeText(
                        this,
                        "Você não possui livros favoritados",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                for (document in documents) {

                    val titulo = document.getString("titulo") ?: ""

                    imageBooks.add(
                        ImageBook(
                            R.color.primary_soft,
                            titulo
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Erro ao carregar livros favoritos",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    /**
     * Pesquisa um livro favorito pelo título
     */
    private fun pesquisarLivro(tituloPesquisa: String) {

        val uid = auth.currentUser?.uid

        if (uid == null) return

        db.collection("estudantes")
            .document(uid)
            .collection("livros_favoritos")
            .orderBy("titulo_lower")
            .startAt(tituloPesquisa)
            .endAt(tituloPesquisa + "\uf8ff")
            .get()

            .addOnSuccessListener { documents ->

                imageBooks.clear()

                if (documents.isEmpty) {

                    Toast.makeText(
                        this,
                        "Livro não encontrado",
                        Toast.LENGTH_SHORT
                    ).show()

                    adapter.notifyDataSetChanged()

                    return@addOnSuccessListener
                }

                for (document in documents) {

                    val titulo = document.getString("titulo") ?: ""

                    imageBooks.add(
                        ImageBook(
                            R.color.primary_soft,
                            titulo
                        )
                    )
                }

                adapter.notifyDataSetChanged()
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Erro ao pesquisar livro",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun configurarPesquisa() {

        inputSearch.setOnEditorActionListener { _, _, _ ->

            val textoPesquisa = inputSearch.text.toString().trim().lowercase()

            if (textoPesquisa.isEmpty()) {

                carregarLivrosFavoritos()

            } else {

                pesquisarLivro(textoPesquisa)
            }

            true
        }
    }

    private fun configurarBottomNavigation() {

        val bottom = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottom.selectedItemId = R.id.nav_books

        bottom.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.nav_home -> {
                    startActivity(
                        Intent(this, HomeActivity::class.java)
                    )
                }

                R.id.nav_chat -> {
                    startActivity(
                        Intent(this, ChatbotActivity::class.java)
                    )
                }

                R.id.nav_notifications -> {
                    startActivity(
                        Intent(this, NotificationsActivity::class.java)
                    )
                }

                R.id.nav_profile -> {
                    startActivity(
                        Intent(this, ProfileActivity::class.java)
                    )
                }
            }

            true
        }
    }
}