package com.lithub.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.admin.AdminHomeActivity
import com.lithub.app.student.HomeActivity
import com.lithub.app.RecoverPasswordActivity

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val register = findViewById<EditText>(R.id.input_matricula)
        val password = findViewById<EditText>(R.id.input_senha)
        val loginBtn = findViewById<Button>(R.id.btn_login)
        val forgotPassword = findViewById<TextView>(R.id.btn_forgot_password)
        val loginError = findViewById<TextView>(R.id.login_error)

        loginBtn.setOnClickListener {

            val matricula = register.text.toString().trim()
            val senha = password.text.toString().trim()
            val email = "$matricula@lithub.com"

            if (!matricula.isEmpty() && !senha.isEmpty()) {

                val auth = FirebaseAuth.getInstance()

                auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {

                            register.setBackgroundResource(R.drawable.rounded_shape_1)
                            password.setBackgroundResource(R.drawable.rounded_shape_1)
                            loginError.visibility = View.GONE


                            Toast.makeText(
                                this,
                                "Seja bem vindo",
                                Toast.LENGTH_SHORT
                            ).show()

                            val uid = auth.currentUser?.uid
                            if (uid != null) {
                                FirebaseFirestore.getInstance().collection("estudantes")
                                    .document(uid)
                                    .update("status", 1)
                                    .addOnCompleteListener {
                                        val intent = Intent(this, HomeActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                            } else {
                                val intent = Intent(this, HomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            }

                        } else {

                            Log.d("LOGIN", "Matrícula ou senha inválida,tente novamente")
                            register.setBackgroundResource(R.drawable.rounded_shape_2)
                            password.setBackgroundResource(R.drawable.rounded_shape_2)
                            loginError.visibility = View.VISIBLE

                            Toast.makeText(
                                this,
                                "Falha no login",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }


                //Usuário de teste
                if (matricula == "1234" && senha == "1234") {

                    register.setBackgroundResource(R.drawable.rounded_shape_1)
                    password.setBackgroundResource(R.drawable.rounded_shape_1)
                    loginError.visibility = View.GONE
                    
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                    Toast.makeText(
                        this,
                        "Seja bem vindo",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                //Admin de teste
                if (matricula == "12345" && senha == "12345") {
                    register.setBackgroundResource(R.drawable.rounded_shape_1)
                    password.setBackgroundResource(R.drawable.rounded_shape_1)
                    loginError.visibility = View.GONE
                    
                    val intent = Intent(this, AdminHomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                    Toast.makeText(
                        this,
                        "ADM TÁ ON",
                        Toast.LENGTH_SHORT
                    ).show()


                }


            }

            else{
                register.setBackgroundResource(R.drawable.rounded_shape_2)
                password.setBackgroundResource(R.drawable.rounded_shape_2)
                loginError.visibility = View.VISIBLE

            }

        }




            forgotPassword.setOnClickListener {
                register.setBackgroundResource(R.drawable.rounded_shape_1)
                password.setBackgroundResource(R.drawable.rounded_shape_1)
                loginError.visibility = View.GONE
                startActivity(Intent(this, RecoverPasswordActivity::class.java))
            }

        }
    }