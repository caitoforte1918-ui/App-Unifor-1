package com.lithub.app.admin

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
import com.lithub.app.adapter.AdminRequestListAdapter
import com.lithub.app.dataclass.Request

class AdminStudentRequestActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var btnSearch: EditText
    private lateinit var adapter: AdminRequestListAdapter
    private val studentRequests = mutableListOf<Request>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_student_request)

        btnSearch = findViewById(R.id.btnSearch)
        db = FirebaseFirestore.getInstance()

        val nomeEstudante = intent.getStringExtra("nome")
        
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewSolicitacoes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = AdminRequestListAdapter(studentRequests)
        recyclerView.adapter = adapter

        if (nomeEstudante != null) {
            fetchStudentRequests(nomeEstudante)
        } else {
            Toast.makeText(this, "Nome do aluno não informado", Toast.LENGTH_SHORT).show()
        }

        // Configuração do botão de pesquisa (EditText com ação de busca no teclado)
        btnSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = btnSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchRequests(query, nomeEstudante)
                } else {
                    if (nomeEstudante != null) {
                        fetchStudentRequests(nomeEstudante)
                    }
                }
                true
            } else {
                false
            }
        }
    }

    private fun fetchStudentRequests(nome: String) {
        db.collection("solicitacoes")
            .whereEqualTo("nome_estudante", nome)
            .whereEqualTo("status", "Em análise")
            .get()
            .addOnSuccessListener { documents ->
                studentRequests.clear()

                if (documents.isEmpty) {
                    Toast.makeText(this, "Nenhuma solicitação em análise para este aluno", Toast.LENGTH_SHORT).show()
                }

                for (document in documents) {
                    studentRequests.add(mapDocumentToRequest(document))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar solicitações", Toast.LENGTH_SHORT).show()
            }
    }

    private fun searchRequests(query: String, nomeEstudante: String?) {
        // Tenta buscar pelo ID do documento primeiro para resolver casos de nomes duplicados
        db.collection("solicitacoes").document(query).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val docStudent = document.getString("nome_estudante")
                    // Verifica se pertence ao estudante atual (se houver filtro por estudante)
                    if (nomeEstudante == null || docStudent == nomeEstudante) {
                        studentRequests.clear()
                        studentRequests.add(mapDocumentToRequest(document))
                        adapter.notifyDataSetChanged()
                        return@addOnSuccessListener
                    }
                }
                // Se não encontrar por ID, realiza a busca por título (case-insensitive e parcial)
                searchByTitle(query, nomeEstudante)
            }
            .addOnFailureListener {
                // Em caso de erro no ID (ex: caracteres inválidos), tenta buscar por título
                searchByTitle(query, nomeEstudante)
            }
    }

    private fun searchByTitle(query: String, nomeEstudante: String?) {
        var baseQuery = db.collection("solicitacoes")
            .whereEqualTo("status", "Em análise")

        if (nomeEstudante != null) {
            baseQuery = baseQuery.whereEqualTo("nome_estudante", nomeEstudante)
        }

        baseQuery.get()
            .addOnSuccessListener { documents ->
                studentRequests.clear()
                val lowerQuery = query.lowercase()

                for (document in documents) {
                    val titulo = document.getString("titulo") ?: ""
                    // Filtro para permitir letras minúsculas e nome incompleto
                    if (titulo.lowercase().contains(lowerQuery)) {
                        studentRequests.add(mapDocumentToRequest(document))
                    }
                }

                adapter.notifyDataSetChanged()
                if (studentRequests.isEmpty()) {
                    Toast.makeText(this, "Nenhuma solicitação encontrada para '$query'", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao pesquisar solicitações", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mapDocumentToRequest(document: com.google.firebase.firestore.DocumentSnapshot): Request {
        return Request(
            id = document.id,
            image = R.color.primary_soft, // Mantendo o placeholder original
            titulo = document.getString("titulo") ?: "",
            autor = document.getString("autor") ?: "",
            estudante = document.getString("nome_estudante") ?: "",
            matricula = document.getString("matricula") ?: "",
            tipo = document.getString("tipo") ?: "",
            status = document.getString("status") ?: "",
            estudanteUid = document.getString("estudante_uid") ?: ""
        )
    }
}
