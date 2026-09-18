package com.lithub.app.admin

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
import com.lithub.app.adapter.CardReadBookListAdapter
import com.lithub.app.dataclass.CardReadBook

class AdminReadBooksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_read_books)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val matricula = intent.getStringExtra("matricula")

        //Volta para a tela de perfil do estudante ao clicar no botão de voltar
        btnBack.setOnClickListener {
            finish()

        }

        //Referência para o botão de pesquisar
        val input_search_books = findViewById<EditText>(R.id.input_search_books)

        //Lista inicial de livros (Dataclass)
        val cardReadBooks = mutableListOf<CardReadBook>()

        //Pega a referência do RecyclerView no layout
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewGames)

        //o LayoutManager define como as informações serão mostradas dentro da lista
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Definição do Adapter, componente que será responsável por colocar os itens da lista no RecyclerView
        var adapter = CardReadBookListAdapter(cardReadBooks)

        //RecyclerView vai usar o adapter como display das informações
        recyclerView.adapter = adapter


        //Cria instância do Firestore
        val db = FirebaseFirestore.getInstance()

        //Checa na coleção se o texto digitado pelo usuário está no banco de dados
        db.collection("visualizacoes")
            .whereEqualTo("matricula_estudante",matricula)
            .get()
            .addOnSuccessListener { documentos ->

                if (documentos.isEmpty){
                    Toast.makeText(
                        this,
                        "Nenhum livro lido",
                        Toast.LENGTH_SHORT).
                    show()

                }
                //Quando a consulta é bem sucedida a lista de livros é atualizada
                cardReadBooks.clear()

                //Extrai cada informação no documento do Firebase
                for (documento in documentos){



                    val title = documento.getString("titulo").toString()
                    val autor = documento.getString("autor").toString()

                    //A lista é atualizada e um livro é adicionado
                    //o livro é preenchido com as infos do banco de dados
                    cardReadBooks.add(
                        CardReadBook(R.color.primary_soft,
                            title,
                            autor,
                            "Status: Lido")
                    )

                }

                //Sinaliza para o RecyclerView que a lista foi atualizada
                adapter.notifyDataSetChanged()


            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Falha ao conectar com banco de dados",
                    Toast.LENGTH_SHORT)
                    .show()
            }


        input_search_books.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH){

                val text = input_search_books.text.toString().trim().lowercase()

                db.collection("visualizacoes")
                    .whereEqualTo("matricula_estudante",matricula)
                    .orderBy("titulo_lower")
                    .startAt(text)
                    .endAt(text + '\uf8ff')
                    .get()
                    .addOnSuccessListener { documents ->

                        if (documents.isEmpty){
                            Toast.makeText(
                                this,
                                "Livro não encontrado",
                                Toast.LENGTH_SHORT).
                            show()

                        }

                        cardReadBooks.clear()

                        for( document in documents){

                            val titulo = document.getString("titulo")
                            val autor = document.getString("autor")

                            cardReadBooks.add(
                                CardReadBook(R.color.primary_soft,
                                    titulo,
                                    autor,
                                    "Status: Lido")

                            )

                        }

                        adapter.notifyDataSetChanged()



                    }


                true
            }
            else{
                false
            }

        }

        



    }
}



