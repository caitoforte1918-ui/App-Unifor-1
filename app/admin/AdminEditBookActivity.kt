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

class AdminEditBookActivity : AppCompatActivity() {

    private lateinit var inputTitle: EditText
    private lateinit var inputAuthor: EditText

    private lateinit var inputGenre: EditText
    private lateinit var inputRating: EditText
    private lateinit var inputStatus: Spinner
    private lateinit var inputSynopsis: EditText
    private lateinit var btnFinishEdit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_book)

        val intentTitulo = intent.getStringExtra("titulo")
        val intentAutor = intent.getStringExtra("autor")
        val intentGenero = intent.getStringExtra("genero")
        val intentAvaliacao = intent.getStringExtra("avaliacao")
        val intentSinopse = intent.getStringExtra("sinopse")
        val intentStatus = intent.getStringExtra("status")


        val btnBack = findViewById<ImageView>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }


        inputTitle = findViewById(R.id.input_title)
        inputAuthor = findViewById(R.id.input_author)
        inputGenre = findViewById(R.id.input_genre)
        inputRating = findViewById(R.id.input_rating)
        inputStatus = findViewById(R.id.input_status)
        inputSynopsis = findViewById(R.id.input_synopsis)
        btnFinishEdit = findViewById(R.id.btn_finish_edit)

        // Configuração do Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.status_options,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputStatus.adapter = adapter

        // Remove o texto "Avaliação: "
        val avaliacaoLimpa = intentAvaliacao
            ?.replace("Avaliação:", "")
            ?.replace("/5.0", "")
            ?.trim()

        inputTitle.setText(intentTitulo)
        inputAuthor.setText(intentAutor)
        inputGenre.setText(intentGenero)
        inputRating.setText(avaliacaoLimpa)
        inputSynopsis.setText(intentSinopse)

        // Define valor selecionado do Spinner
        val spinnerPosition = adapter.getPosition(intentStatus)
        inputStatus.setSelection(spinnerPosition)

        val db = FirebaseFirestore.getInstance()

        btnFinishEdit.setOnClickListener {

            val updateLivro = hashMapOf(
                "titulo" to inputTitle.text.toString().trim(),
                "titulo_lower" to inputTitle.text.toString().trim().lowercase(),
                "autor" to inputAuthor.text.toString().trim(),
                "genero" to inputGenre.text.toString().trim(),
                "avaliacao" to inputRating.text.toString().trim(),
                "sinopse" to inputSynopsis.text.toString().trim(),
                "status" to inputStatus.selectedItem.toString()
            )

            db.collection("livros")
                .whereEqualTo("titulo", intentTitulo)
                .whereEqualTo("autor", intentAutor)
                .get()
                .addOnSuccessListener { documents ->

                    if (!documents.isEmpty) {

                        val document = documents.documents[0]

                        db.collection("livros")
                            .document(document.id)
                            .update(updateLivro as Map<String, Any>)
                            .addOnSuccessListener {

                                Toast.makeText(
                                    this,
                                    "Livro atualizado com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                finish()
                            }
                            .addOnFailureListener {

                                Toast.makeText(
                                    this,
                                    "Erro ao atualizar",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                    } else {

                        Toast.makeText(
                            this,
                            "Livro não encontrado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    return@addOnSuccessListener
                }
                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Erro ao buscar livro",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}