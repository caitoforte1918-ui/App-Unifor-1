package com.lithub.app.admin

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R

class AdminEditGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_edit_game)

        //Configuração dos botões
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnFinish = findViewById<Button>(R.id.btn_finish_edit)

        btnBack.setOnClickListener {
            finish()
        }

        //Declaração dos campos
        val input_title = findViewById<EditText>(R.id.input_title)
        val input_desc = findViewById<EditText>(R.id.input_description)
        val input_rules = findViewById<EditText>(R.id.input_rules)
        val input_status = findViewById<Spinner>(R.id.input_status)

        //Declaração das Intents
        val titulo = intent.getStringExtra("titulo")
        val descricao = intent.getStringExtra("descricao")
        val regras = intent.getStringExtra("regras")
        val status = intent.getStringExtra("status")

        // Configuração do Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.status_options,
            android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        input_status.adapter = adapter

        //Configura os campos com os valores da Intent passada no AdminGameDetailActivity
        input_title.setText(titulo)
        input_desc.setText(descricao)
        input_rules.setText(regras)
        val spinnerPosition = adapter.getPosition(status)
        input_status.setSelection(spinnerPosition)

        val db = FirebaseFirestore.getInstance()


        btnFinish.setOnClickListener {

            val updateJogo = hashMapOf(
                "titulo" to input_title.text.toString().trim(),
                "titulo_lower" to input_title.text.toString().trim().lowercase(),
                "descricao" to input_desc.text.toString().trim(),
                "regras" to input_rules.text.toString().trim(),
                "status" to input_status.selectedItem.toString()
            )

            db.collection("jogos")
                .whereEqualTo("titulo", titulo)
                .get()
                .addOnSuccessListener { documents ->

                    if (documents.isEmpty) {

                        Toast.makeText(
                            this,
                            "Não foi possível alterar os dados do jogo",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    val document = documents.documents[0]

                    db.collection("jogos")
                        .document(document.id)
                        .update(updateJogo as Map<String, Any>)
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Jogo atualizado com sucesso!",
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
                }
                .addOnFailureListener {

                    Toast.makeText(
                        this,
                        "Erro ao buscar jogo",
                        Toast.LENGTH_SHORT
                    ).show()
                }

        }







    }
}