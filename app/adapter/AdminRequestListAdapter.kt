package com.lithub.app.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.R
import com.lithub.app.admin.AdminRecordsActivity
import com.lithub.app.dataclass.Request
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminRequestListAdapter(val requests: List<Request>): RecyclerView.Adapter<AdminRequestListAdapter.AdminRequestViewHolder>() {

    class AdminRequestViewHolder(view: View): RecyclerView.ViewHolder(view){

        val image = view.findViewById<ImageView>(R.id.imgBookSolicit)
        val title = view.findViewById<TextView>(R.id.tvTitulo)
        val autor = view.findViewById<TextView>(R.id.tvAutor)
        val estudante = view.findViewById<TextView>(R.id.tvEstudante)
        val matricula = view.findViewById<TextView>(R.id.tvMatricula)
        val tipo = view.findViewById<TextView>(R.id.tvTipo)
        val status = view.findViewById<TextView>(R.id.tvStatus)

        val btnAceitar = view.findViewById<Button>(R.id.btnAceitar)
        val btnRecusar = view.findViewById<Button>(R.id.btnRecusar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
        return AdminRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminRequestViewHolder, position: Int) {
        val request = requests[position]

        holder.title.text = request.titulo
        holder.autor.text = request.autor
        holder.estudante.text = "Solicitação de: ${request.estudante}"
        holder.matricula.text = "Matrícula: ${request.matricula}"
        holder.tipo.text = "Solicitação de: ${request.tipo}"
        holder.status.text = "Status: ${request.status}"
        holder.image.setImageResource(request.image)

        val context = holder.itemView.context

        holder.btnAceitar.setOnClickListener {
            actionDialog(context, request, true)
        }

        holder.btnRecusar.setOnClickListener {
            actionDialog(context, request, false)
        }
    }

    override fun getItemCount(): Int {
        return requests.size
    }

    fun actionDialog(context: Context, request: Request, isAccepting: Boolean){

        val acao = if (isAccepting) "aceitar" else "recusar"
        val novoStatus = if (isAccepting) "Aceito" else "Recusado"

        val dialog = AlertDialog.Builder(context)
            .setTitle("Confirmar Ação")
            .setMessage("Deseja realmente $acao a solicitação de '${request.titulo}'?")
            .setPositiveButton("Sim") { d, _ ->
                val db = FirebaseFirestore.getInstance()
                db.collection("solicitacoes").document(request.id)
                    .update("status", novoStatus)
                    .addOnSuccessListener {

                        if (isAccepting && request.estudanteUid.isNotEmpty()) {
                            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                            val dataAtual = sdf.format(Date())

                            val emprestimo = hashMapOf(
                                "titulo" to request.titulo,
                                "autor" to request.autor,
                                "data_aceito" to dataAtual
                            )

                            db.collection("estudantes")
                                .document(request.estudanteUid)
                                .collection("emprestimos")
                                .add(emprestimo)
                        }

                        Toast.makeText(context, "Solicitação $novoStatus", Toast.LENGTH_SHORT).show()
                        d.dismiss()
                        context.startActivity(Intent(context, AdminRecordsActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Erro ao atualizar status", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar") { d, _ ->
                d.dismiss()
            }
            .create()

        dialog.show()
    }
}