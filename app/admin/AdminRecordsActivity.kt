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
import com.lithub.app.adapter.AdminRecordListAdapter
import com.lithub.app.dataclass.Record

class AdminRecordsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_records)

        recyclerView = findViewById(R.id.recyclerViewRecords)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val inputSearch = findViewById<EditText>(R.id.input_search_records)

        // Busca inicial de todos os registros Aceitos/Recusados
        fetchRecords("")

        inputSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = v.text.toString().lowercase().trim()
                fetchRecords(searchText)
                true
            } else {
                false
            }
        }

        val bottomAdmin = findViewById<BottomNavigationView>(R.id.bottomAdmin)
        bottomAdmin.selectedItemId = R.id.nav_admin_records
        bottomAdmin.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_admin_home -> startActivity(Intent(this, AdminHomeActivity::class.java))
                R.id.nav_admin_collection -> startActivity(Intent(this, AdminCollectionActivity::class.java))
                R.id.nav_admin_requests -> startActivity(Intent(this, AdminRequestActivity::class.java))
            }
            true
        }
    }

    private fun fetchRecords(searchText: String) {
        db.collection("solicitacoes")
            .whereIn("status", listOf("Aceito", "Recusado"))
            .get()
            .addOnSuccessListener { result ->
                val recordsList = mutableListOf<Record>()
                val lowerSearch = searchText.lowercase().trim()

                for (document in result) {
                    val id = document.id
                    val titulo = document.getString("titulo") ?: ""
                    val tituloLower = titulo.lowercase()

                    // Filtra por ID ou Título (contém e ignorando maiúsculas/minúsculas)
                    if (lowerSearch.isEmpty() || id.lowercase().contains(lowerSearch) || tituloLower.contains(lowerSearch)) {
                        val tipo = document.getString("tipo") ?: ""
                        val estudante = document.getString("nome_estudante") ?: ""
                        val matricula = document.getString("matricula") ?: ""
                        val status = document.getString("status") ?: ""

                        val autor = if (tipo.equals("Livro", ignoreCase = true)) {
                            document.getString("autor_livro") ?: document.getString("autor") ?: ""
                        } else ""

                        recordsList.add(
                            Record(
                                id = id,
                                image = R.color.primary_soft,
                                titulo = titulo,
                                autor = autor,
                                estudante = estudante,
                                matricula = matricula,
                                tipo = tipo,
                                status = status
                            )
                        )
                    }
                }
                recyclerView.adapter = AdminRecordListAdapter(recordsList)
            }
    }
}
