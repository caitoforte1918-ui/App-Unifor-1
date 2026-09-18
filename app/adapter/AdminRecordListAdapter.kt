package com.lithub.app.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lithub.app.R
import com.lithub.app.dataclass.Record
class  AdminRecordListAdapter(val records: List<Record>): RecyclerView.Adapter<AdminRecordListAdapter.AdminRecordViewHolder>() {

    class AdminRecordViewHolder(view: View): RecyclerView.ViewHolder(view){

        val image = view.findViewById<ImageView>(R.id.imgBookRecord)
        val title = view.findViewById<TextView>(R.id.tvTitulo)
        val autor = view.findViewById<TextView>(R.id.tvAutor)
        val estudante = view.findViewById<TextView>(R.id.tvEstudante)
        val matricula = view.findViewById<TextView>(R.id.tvMatricula)
        val tipo = view.findViewById<TextView>(R.id.tvTipo)
        val status = view.findViewById<TextView>(R.id.tvStatus)
        val btnStatus = view.findViewById<Button>(R.id.btnStatus)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminRecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false)
        return AdminRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminRecordViewHolder, position: Int) {

        val record = records[position]

        holder.title.text = record.titulo
        holder.autor.text = record.autor
        holder.estudante.text = "Solicitação de: ${record.estudante}"
        holder.matricula.text = "Matrícula: ${record.matricula}"
        holder.tipo.text = "Solicitação de: ${record.tipo}"
        holder.status.text = "Status: ${record.status}"
        holder.image.setImageResource(record.image)

        holder.btnStatus.text = record.status

        if (record.status.equals("Aceito", ignoreCase = true)) {
            holder.btnStatus.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#309940"))
        } else if (record.status.equals("Recusado", ignoreCase = true)) {
            holder.btnStatus.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#BD4A47"))
        }
    }

    override fun getItemCount(): Int {
        return records.size
    }


}