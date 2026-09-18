package com.lithub.app.admin

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R
import com.lithub.app.adapter.AdminRequestListAdapter
import com.lithub.app.dataclass.Request

class AdminRequestActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_request)


        recyclerView = findViewById(R.id.recyclerViewSolicitacoes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val inputSearch = findViewById<EditText>(R.id.input_search_requests)

        // Busca inicial de todas as solicitações em análise
        fetchRequests("")

        inputSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = v.text.toString().lowercase().trim()
                fetchRequests(searchText)
                true
            } else {
                false
            }
        }

        val bottomAdmin = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomAdmin.selectedItemId = R.id.nav_admin_requests

        bottomAdmin.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_admin_home -> {
                    startActivity(Intent(this, AdminHomeActivity::class.java))
                    true
                }

                R.id.nav_admin_requests -> true

                R.id.nav_admin_records -> {
                    startActivity(Intent(this, AdminRecordsActivity::class.java))
                    true
                }

                R.id.nav_admin_collection -> {
                    startActivity(Intent(this, AdminCollectionActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchRequests(searchText: String) {
        db.collection("solicitacoes")
            .whereEqualTo("status", "Em análise")
            .get()
            .addOnSuccessListener { result ->
                val listaSolicitacoes = mutableListOf<Request>()
                val lowerSearch = searchText.lowercase().trim()

                for (document in result) {
                    val id = document.id
                    val titulo = document.getString("titulo") ?: ""
                    val tituloLower = titulo.lowercase()

                    // Filtra por ID ou Título (contém e ignorando maiúsculas/minúsculas)
                    if (searchText.isEmpty() || id.lowercase().contains(lowerSearch) || tituloLower.contains(lowerSearch)) {
                        val tipo = document.getString("tipo") ?: ""
                        val estudante = document.getString("nome_estudante") ?: ""
                        val matricula = document.getString("matricula") ?: ""
                        val status = document.getString("status") ?: ""

                        if (tipo.equals("Livro", ignoreCase = true)) {
                            val autor = document.getString("autor_livro") ?: document.getString("autor") ?: ""
                            listaSolicitacoes.add(
                                Request(
                                    id = id,
                                    image = R.color.primary_soft,
                                    titulo = titulo,
                                    autor = autor,
                                    estudante = estudante,
                                    matricula = matricula,
                                    tipo = "Livro",
                                    status = status,
                                    estudanteUid = document.getString("estudante_uid") ?: ""
                                )
                            )
                        } else if (tipo.equals("Jogo", ignoreCase = true)) {
                            listaSolicitacoes.add(
                                Request(
                                    id = id,
                                    image = R.color.primary_soft,
                                    titulo = titulo,
                                    autor = "",
                                    estudante = estudante,
                                    matricula = matricula,
                                    tipo = "Jogo",
                                    status = status,
                                    estudanteUid = document.getString("estudante_uid") ?: ""
                                )
                            )
                        }
                    }
                }
                recyclerView.adapter = AdminRequestListAdapter(listaSolicitacoes)
            }
    }
}
