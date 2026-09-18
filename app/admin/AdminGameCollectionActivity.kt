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
import com.lithub.app.R
import com.lithub.app.adapter.AdminCardGameListAdapter
import com.lithub.app.dataclass.Game

class AdminGameCollectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminCardGameListAdapter
    private lateinit var db: FirebaseFirestore
    private var registration: ListenerRegistration? = null

    private val cardGames = mutableListOf<Game>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_game_collection)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }


        recyclerView = findViewById(R.id.recyclerViewGames)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdminCardGameListAdapter(cardGames)
        recyclerView.adapter = adapter

        db = FirebaseFirestore.getInstance()

        // Carrega todos os jogos
        carregarJogos()

        val inputSearchGames = findViewById<EditText>(R.id.input_search_games)

        inputSearchGames.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val texto = inputSearchGames.text.toString()
                    .lowercase()
                    .trim()

                if (texto.isNotEmpty()) {
                    pesquisarJogos(texto)
                } else {
                    carregarJogos()
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

    // Função responsável por carregar todos os jogos
    private fun carregarJogos() {
        registration?.remove()
        registration = db.collection("jogos")
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

    // Função responsável pela pesquisa
    private fun pesquisarJogos(texto: String) {
        registration?.remove()
        registration = db.collection("jogos")
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

    // Atualiza os dados da RecyclerView
    private fun atualizarLista(documentos: com.google.firebase.firestore.QuerySnapshot) {

        cardGames.clear()

        for (documento in documentos) {

            val title = documento.getString("titulo").toString()
            val desc = documento.getString("descricao").toString()
            val regras = documento.getString("regras").toString()

            cardGames.add(
                Game(
                    title,
                    desc,
                    R.color.primary_soft,
                    regras
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