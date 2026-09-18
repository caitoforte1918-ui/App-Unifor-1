package com.lithub.app

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RecoverPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        val back = findViewById<TextView>(R.id.tvBackLogin)
        val btnBack = findViewById<View>(R.id.btnBack)
        val email = findViewById<EditText>(R.id.etEmail)
        val btn = findViewById<Button>(R.id.btnRecover)
        val title = findViewById<TextView>(R.id.tvRecoverErrorTitle)
        val body = findViewById<TextView>(R.id.tvRecoverErrorBody)

        back.setOnClickListener { finish() }
        btnBack.setOnClickListener { finish() }

        btn.setOnClickListener {
            val emailStr = email.text.toString().trim()

            if (emailStr.isEmpty()) {
                email.error = "Por favor, digite seu e-mail"
                return@setOnClickListener
            }

            // Esconde mensagens anteriores antes de nova tentativa
            title.visibility = View.GONE
            body.visibility = View.GONE

            checkEmailAndRecover(emailStr, email, title, body)
        }
    }

    private fun checkEmailAndRecover(
        emailStr: String,
        emailField: EditText,
        title: TextView,
        body: TextView
    ) {
        val db = FirebaseFirestore.getInstance()

        // 1. Pesquisa na coleção "estudantes" pelo campo "email"
        db.collection("estudantes")
            .whereEqualTo("email", emailStr)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && !task.result.isEmpty) {
                    // 2. Se encontrou o estudante, prossegue com a recuperação de senha
                    sendFirebasePasswordReset(emailStr, emailField, title, body)
                } else {
                    // 3. Se não encontrou ou houve erro na consulta
                    showErrorMessage(
                        emailField,
                        title,
                        body,
                        "E-mail inválido",
                        "Nenhuma conta foi cadastrada com esse e-mail."
                    )
                }
            }
    }

    private fun sendFirebasePasswordReset(
        emailStr: String,
        emailField: EditText,
        title: TextView,
        body: TextView
    ) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(emailStr)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sucesso no envio do e-mail
                    title.text = "E-mail enviado!"
                    title.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
                    body.text = "O e-mail de recuperação foi enviado com sucesso para: $emailStr"

                    title.visibility = View.VISIBLE
                    body.visibility = View.VISIBLE
                    emailField.setBackgroundResource(R.drawable.rounded_shape_1)

                    Toast.makeText(this, "E-mail de recuperação enviado", Toast.LENGTH_SHORT).show()
                } else {
                    // Erro ao processar o envio pelo Firebase Auth
                    showErrorMessage(
                        emailField,
                        title,
                        body,
                        "Erro na recuperação",
                        "Não foi possível enviar o e-mail no momento. Tente novamente mais tarde."
                    )
                }
            }
    }

    private fun showErrorMessage(
        emailField: EditText,
        title: TextView,
        body: TextView,
        errorTitle: String,
        errorBody: String
    ) {
        emailField.setBackgroundResource(R.drawable.rounded_shape_2)
        title.text = errorTitle
        title.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
        body.text = errorBody

        title.visibility = View.VISIBLE
        body.visibility = View.VISIBLE
    }
}