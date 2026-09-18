package com.lithub.app.student

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.lithub.app.R
import com.lithub.app.adapter.CardBookHomeListAdapter
import com.lithub.app.dataclass.CardBook

class HomeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val books = mutableListOf<CardBook>()
    private lateinit var adapter: CardBookHomeListAdapter
    private lateinit var inputSearch: EditText

    private lateinit var popularBooks: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        inputSearch = findViewById(R.id.input_search)
        popularBooks = findViewById(R.id.tvPopularBooks)

        findViewById<Button>(R.id.btnRentGames).setOnClickListener { startActivity(Intent(this, GamesActivity::class.java)) }
        findViewById<Button>(R.id.btnRentBooks).setOnClickListener { startActivity(Intent(this, BooksActivity::class.java)) }

        setupRecyclerView()
        carregarLivros()
        configurarPesquisa()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_books -> startActivity(Intent(this, MyBooksActivity::class.java))
                R.id.nav_chat -> startActivity(Intent(this, ChatbotActivity::class.java))
                R.id.nav_notifications -> startActivity(Intent(this, NotificationsActivity::class.java))
                R.id.nav_profile -> startActivity(Intent(this, ProfileActivity::class.java))
            }
            true
        }
    }

    private fun setupRecyclerView() {
        val rvBooks = findViewById<RecyclerView>(R.id.rvBooks)
        
        // Configuração para ser horizontal
        rvBooks.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        
        adapter = CardBookHomeListAdapter(books)
        rvBooks.adapter = adapter
    }

    private fun carregarLivros() {

        popularBooks.visibility = View.VISIBLE

        // Ordenação por visualizações em ordem decrescente
        db.collection("livros")
            .whereGreaterThan("visualizacoes", 0)
            .orderBy("visualizacoes", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { documents ->
                books.clear()

                if (documents.isEmpty) {
                    Toast.makeText(this, "Nenhum livro encontrado", Toast.LENGTH_SHORT).show()
                }


                for (document in documents) {
                    val title = document.getString("titulo") ?: ""
                    val author = document.getString("autor") ?: ""
                    val synopsis = document.getString("sinopse") ?: ""
                    val status = document.getString("status") ?: "Disponível"
                    val visualizacoes = document.getLong("visualizacoes")?.toInt() ?: 0
                    
                    books.add(
                        CardBook(
                            title,
                            author,
                            synopsis,
                            R.color.primary_soft,
                            status,
                            visualizacoes
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar recomendações", Toast.LENGTH_SHORT).show()
            }
    }

    private fun configurarPesquisa() {
        inputSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val textoPesquisa = inputSearch.text.toString().trim().lowercase()
                
                if (textoPesquisa.isEmpty()) {
                    carregarLivros()
                } else {
                    pesquisarLivros(textoPesquisa)
                    popularBooks.visibility = View.GONE
                }
                true
            } else {
                false
            }
        }
    }

    private fun pesquisarLivros(query: String) {

        
        db.collection("livros")
            .orderBy("titulo_lower")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                books.clear()
                if (documents.isEmpty) {
                    Toast.makeText(this, "Nenhum livro encontrado", Toast.LENGTH_SHORT).show()
                } else {
                    for (document in documents) {
                        val title = document.getString("titulo") ?: ""
                        val author = document.getString("autor") ?: ""
                        val synopsis = document.getString("sinopse") ?: ""
                        val status = document.getString("status") ?: "Disponível"
                        val visualizacoes = document.getLong("visualizacoes")?.toInt() ?: 0

                        books.add(
                            CardBook(
                                title,
                                author,
                                synopsis,
                                R.color.primary_soft,
                                status,
                                visualizacoes
                            )
                        )
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro na pesquisa", Toast.LENGTH_SHORT).show()
            }
    }
}