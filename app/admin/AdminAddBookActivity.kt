package com.lithub.app.admin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R

class AdminAddBookActivity : AppCompatActivity() {

    private lateinit var inputTitle: EditText
    private lateinit var inputAuthor: EditText
    private lateinit var inputRating: EditText
    private lateinit var inputStatus: Spinner
    private lateinit var inputSynopsis: EditText
    private lateinit var btnCreateBook: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_book)


        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // Inicializando componentes
        inputTitle = findViewById(R.id.input_title)
        inputAuthor = findViewById(R.id.input_author)
        inputRating = findViewById(R.id.input_rating)
        inputStatus = findViewById(R.id.input_status)
        inputSynopsis = findViewById(R.id.input_synopsis)

        // Configurando o Spinner de Status
        val statusOptions = arrayOf("Desbloqueado", "Bloqueado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputStatus.adapter = adapter

        btnCreateBook = findViewById(R.id.btn_create_book)

        // Clique do botão
        btnCreateBook.setOnClickListener {

            val title = inputTitle.text.toString().trim()
            val author = inputAuthor.text.toString().trim()
            val rating = inputRating.text.toString().trim()
            val status = inputStatus.selectedItem.toString()
            val synopsis = inputSynopsis.text.toString().trim()

            // Verifica se algum campo está vazio
            if (title.isEmpty() ||
                author.isEmpty() ||
                rating.isEmpty() ||
                status.isEmpty() ||
                synopsis.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Preencha todas as informações!",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val livro = hashMapOf(
                    "titulo" to title,
                    "titulo_lower" to title.lowercase(),
                    "autor" to author,
                    "avaliacao" to rating,
                    "status" to status,
                    "sinopse" to synopsis,
                    "visualizacoes" to 0

                )

                val db = FirebaseFirestore.getInstance()

                db.collection("livros")
                    .add(livro)
                    .addOnSuccessListener {  documentReference ->
                        println("Documento criado com ID: ${documentReference.id}")

                        Toast.makeText(
                            this,
                            "Livro adicinado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    .addOnFailureListener { e ->
                        println("Erro ao adicionar documento")

                        Toast.makeText(
                            this,
                            "Erro ao adicionar livro",
                            Toast.LENGTH_SHORT
                        ).show()
                    }


            }
        }
    }
}