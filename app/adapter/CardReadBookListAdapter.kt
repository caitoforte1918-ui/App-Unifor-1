package com.lithub.app.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lithub.app.R
import com.lithub.app.admin.AdminBookDetailActivity
import com.lithub.app.dataclass.CardReadBook

class CardReadBookListAdapter(val readbooks: List<CardReadBook>): RecyclerView.Adapter<CardReadBookListAdapter.CardReadBookViewHolder>() {

    class CardReadBookViewHolder(view: View): RecyclerView.ViewHolder(view){

        val image = view.findViewById<ImageView>(R.id.imageBook)
        val title = view.findViewById<TextView>(R.id.txtTitle)
        val autor = view.findViewById<TextView>(R.id.txtAuthor)
        val status = view.findViewById<TextView>(R.id.txtStatus)



    }



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CardReadBookListAdapter.CardReadBookViewHolder {

        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_readbook,p0,false)

        return CardReadBookViewHolder(view)

    }

    override fun onBindViewHolder(p0: CardReadBookViewHolder, p1: Int
    ) {

        val readbook = readbooks[p1]

        p0.image.setImageResource(R.color.primary_soft)
        p0.title.text = readbook.titulo
        p0.autor.text = readbook.autor
        p0.status.text = readbook.status


        p0.itemView.setOnClickListener {

            val context = p0.itemView.context
            val intent = Intent(context, AdminBookDetailActivity::class.java)
            intent.putExtra("tituloAdapter",readbook.titulo)
            intent.putExtra("autorAdapter", readbook.autor)



            context.startActivity(intent)

        }

    }




    override fun getItemCount(): Int {
        return readbooks.size
    }
}