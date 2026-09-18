package com.lithub.app.student

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R
import com.lithub.app.adapter.CardGameListAdapter
import com.lithub.app.dataclass.Game

class GamesActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_games)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewGames)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val db = FirebaseFirestore.getInstance()

        val cardGames = mutableListOf<Game>()

        val adapter = CardGameListAdapter(cardGames)

        recyclerView.adapter = adapter

        carregarJogos(db, cardGames, adapter)


        val input_search_games = findViewById<EditText>(R.id.input_search_games)

        input_search_games.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val text = input_search_games.text.toString().lowercase().trim()

                pesquisarJogos(text,db,cardGames,adapter)


                true
            } else {
                false
            }


        }


    }

    private fun carregarJogos(db: FirebaseFirestore, cardGames: MutableList<Game>, adapter: CardGameListAdapter) {

        db.collection("jogos")
            .get()
            .addOnSuccessListener { documentos ->

                cardGames.clear()

                for (documento in documentos) {

                    val title = documento.getString("titulo") ?: ""
                    val desc = documento.getString("descricao") ?: ""
                    val regras = documento.getString("regras") ?: ""

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
            .addOnFailureListener { e ->

                Toast.makeText(
                    this,
                    "Erro: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
    private fun pesquisarJogos(texto: String, db: FirebaseFirestore, cardGames: MutableList<Game>, adapter: CardGameListAdapter) {

        val pesquisa = texto.lowercase().trim()

        if (pesquisa.isEmpty()) {
            carregarJogos(db, cardGames, adapter)
            return
        }

        db.collection("jogos")
            .orderBy("titulo_lower")
            .startAt(pesquisa)
            .endAt(pesquisa + "\uf8ff")
            .get()
            .addOnSuccessListener { documentos ->

                cardGames.clear()

                for (documento in documentos) {

                    val title = documento.getString("titulo") ?: ""
                    val desc = documento.getString("descricao") ?: ""
                    val regras = documento.getString("regras") ?: ""

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
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Falha ao conectar com banco de dados",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}