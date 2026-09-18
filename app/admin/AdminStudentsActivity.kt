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
import com.lithub.app.adapter.StudentListAdapter
import com.lithub.app.dataclass.Student

class AdminStudentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_students)

        val students = mutableListOf<Student>()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewGames)
        val adapter = StudentListAdapter(students)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        val searchStudent = findViewById<EditText>(R.id.input_search_student)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }


        val db = FirebaseFirestore.getInstance()

        db.collection("estudantes")
            .get()
            .addOnSuccessListener { documents ->

                for (documento in documents){

                    val nome = documento.getString("nome")
                    val curso = documento.getString("curso")

                    students.add(
                        Student(R.color.primary_soft,nome,curso)
                    )



                }

                adapter.notifyDataSetChanged()


            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Erro ao carregar estudantes",
                    Toast.LENGTH_SHORT
                ).show()
            }

        searchStudent.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH){

                val pesquisaNome = searchStudent.text.toString().trim().lowercase()

                db.collection("estudantes")
                    .orderBy("nome_lower")
                    .startAt(pesquisaNome)
                    .endAt(pesquisaNome + "\uf8ff")
                    .get()
                    .addOnSuccessListener { documents ->

                        students.clear()


                        for (document in documents){

                            val nome = document.getString("nome")
                            val curso = document.getString("curso")


                            students.add(
                                Student(R.color.primary_soft,
                                    nome,
                                    curso)


                            )

                        }

                        adapter.notifyDataSetChanged()
                    }



                true
            }
            else
            {
                false
            }


        }






    }
}