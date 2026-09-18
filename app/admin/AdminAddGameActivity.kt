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

class AdminAddGameActivity : AppCompatActivity() {

    private lateinit var inputTitle: EditText
    private lateinit var inputStatus: Spinner
    private lateinit var inputDescription: EditText
    private lateinit var inputRules: EditText
    private lateinit var btnCreateGame: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_game)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }


        // Inicializando componentes
        inputTitle = findViewById(R.id.input_title)
        inputStatus = findViewById(R.id.input_status)
        inputDescription = findViewById(R.id.input_description)
        inputRules = findViewById(R.id.input_rules)
        btnCreateGame = findViewById(R.id.btn_create_game)

        // Configurando o Spinner de Status
        val statusOptions = arrayOf("Desbloqueado", "Bloqueado")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        inputStatus.adapter = adapter

        // Clique do botão
        btnCreateGame.setOnClickListener {

            val title = inputTitle.text.toString().trim()
            val status = inputStatus.selectedItem.toString()
            val description = inputDescription.text.toString().trim()
            val rules = inputRules.text.toString().trim()

            // Verificação dos campos
            if (title.isEmpty() ||
                status.isEmpty() ||
                description.isEmpty() ||
                rules.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Preencha todas as informações!",
                    Toast.LENGTH_SHORT
                ).show()

            }  else {

                val jogo = hashMapOf(
                    "titulo" to title,
                    "titulo_lower" to title.lowercase(),
                    "status" to status,
                    "descricao" to description,
                    "regras" to rules
                )

                val db = FirebaseFirestore.getInstance()

                db.collection("jogos")
                    .add(jogo)
                    .addOnSuccessListener {  documentReference ->
                        println("Documento criado com ID: ${documentReference.id}")

                        Toast.makeText(
                            this,
                            "Jogo adicinado com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                    .addOnFailureListener { _ ->
                        println("Erro ao adicionar documento")

                        Toast.makeText(
                            this,
                            "Erro ao adicionar Jogo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                finish()
            }
        }
    }
}