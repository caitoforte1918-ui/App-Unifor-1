package com.lithub.app.admin
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R

class AdminStudentProfileActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_student_profile)

        //Perfil
        val txtNome = findViewById<TextView>(R.id.txtNome)
        val txtCurso = findViewById<TextView>(R.id.txtCurso)

        //Visão Geral
        val txtLidos = findViewById<TextView>(R.id.txtLidos)
        val txtAvaliacoes = findViewById<TextView>(R.id.txtAvaliacoes)
        val txtSolicitacoes = findViewById<TextView>(R.id.txtSolicitacoes)

        //Detalhes Pessoais
        val txtMatricula = findViewById<TextView>(R.id.txtMatricula)
        val txtEmail = findViewById<TextView>(R.id.txtEmail)

        var matriculaEstudante = ""

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        db.collection("estudantes")
            .whereEqualTo("nome",intent.getStringExtra("nome"))
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents){

                    val nome = document.getString("nome")
                    val curso = document.getString("curso")

                    val matricula = document.getString("matricula")
                    val email = document.getString("email")


                    matriculaEstudante = document.getString("matricula").toString()

                    txtNome.text = nome
                    txtCurso.text = curso

                    txtMatricula.text = "Matrícula: $matricula"
                    txtEmail.text = "Email: $email"

                    if (matricula != null) {
                        contarLivrosLidos(matricula, txtLidos)
                        contarAvaliacoes(matricula, txtAvaliacoes)
                        contarSolicitacoes(matricula, txtSolicitacoes)
                        buscarEmprestimosPorMatricula(matricula)
                    }
                }






            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Falha ao ler dados do estudante",
                    Toast.LENGTH_SHORT)
                    .show()
            }




        findViewById<LinearLayout>(R.id.btnAdminReadBooks).setOnClickListener {
            val intent = Intent(this, AdminReadBooksActivity::class.java)
            intent.putExtra("matricula",matriculaEstudante)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.btnAdminRequests).setOnClickListener {
            val intent = Intent(this, AdminStudentRequestActivity::class.java)
            intent.putExtra("nome",txtNome.text.toString())
            intent.putExtra("matricula", matriculaEstudante)
            startActivity(intent)

        }

        findViewById<LinearLayout>(R.id.btnAdminStudentReview).setOnClickListener {
            val intent = Intent(this, AdminStudentReviewActivity::class.java)
            intent.putExtra("nome",txtNome.text.toString())
            intent.putExtra("matricula", matriculaEstudante)
            startActivity(intent)
        }

    }

    private fun contarLivrosLidos(matricula: String, textView: TextView) {
        db.collection("visualizacoes")
            .whereEqualTo("matricula_estudante", matricula)
            .get()
            .addOnSuccessListener { documents ->
                textView.text = documents.size().toString()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha ao contar livros lidos", Toast.LENGTH_SHORT).show()
                textView.text = "0"
            }
    }

    private fun contarAvaliacoes(matricula: String, textView: TextView) {
        db.collection("livros").get().addOnSuccessListener { livros ->
            var totalAvaliacoes = 0
            var livrosProcessados = 0

            if (livros.isEmpty) {
                textView.text = "0"
                return@addOnSuccessListener
            }

            for (livro in livros) {
                livro.reference.collection("avaliacoes")
                    .whereEqualTo("matricula", matricula)
                    .get()
                    .addOnSuccessListener { avaliacoes ->
                        totalAvaliacoes += avaliacoes.size()
                        livrosProcessados++

                        if (livrosProcessados == livros.size()) {
                            textView.text = totalAvaliacoes.toString()
                        }
                    }
                    .addOnFailureListener {
                        livrosProcessados++
                        if (livrosProcessados == livros.size()) {
                            textView.text = totalAvaliacoes.toString()
                        }
                    }
            }
        }.addOnFailureListener {
            textView.text = "0"
        }
    }

    private fun contarSolicitacoes(matricula: String, textView: TextView) {
        db.collection("solicitacoes")
            .whereEqualTo("matricula", matricula)
            .get()
            .addOnSuccessListener { documents ->
                textView.text = documents.size().toString()
            }
    }

    private fun buscarEmprestimosPorMatricula(matricula: String) {
        val layoutHistorico = findViewById<LinearLayout>(R.id.LoanHistoryLinearLayout)
        layoutHistorico.removeAllViews()

        db.collection("estudantes")
            .whereEqualTo("matricula", matricula)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.collection("emprestimos")
                        .get()
                        .addOnSuccessListener { emprestimos ->
                            val listaEmprestimos = mutableListOf<String>()
                            for (docEmprestimo in emprestimos) {
                                val titulo = docEmprestimo.getString("titulo") ?: ""
                                val data = docEmprestimo.getString("data_aceito") ?: ""
                                listaEmprestimos.add("$titulo - $data")
                            }

                            for (item in listaEmprestimos) {
                                val textView = TextView(this)
                                textView.text = item
                                textView.textSize = 14f
                                textView.typeface = ResourcesCompat.getFont(this, R.font.montserrat_light)
                                textView.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
                                textView.setPadding(0, 10, 0, 10)
                                layoutHistorico.addView(textView)
                            }
                        }
                }
            }
    }
}