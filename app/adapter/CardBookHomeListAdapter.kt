package com.lithub.app.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.lithub.app.student.BookDetailsActivity
import com.lithub.app.dataclass.CardBook
import com.lithub.app.R
import com.lithub.app.admin.AdminBookDetailActivity

class CardBookHomeListAdapter(private val cardBooks: List<CardBook>): RecyclerView.Adapter<CardBookHomeListAdapter.CardBookHomeViewHolder>(){

    class CardBookHomeViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val img = view.findViewById<ImageView>(R.id.imgBook)
        val title = view.findViewById<TextView>(R.id.txtTitle)
        val author = view.findViewById<TextView>(R.id.txtAuthor)
        val synopsis = view.findViewById<TextView>(R.id.txtSynopsis)




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardBookHomeViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.item_cardbook,parent,false)

        return CardBookHomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardBookHomeViewHolder, position: Int) {

        val book = cardBooks[position]

        holder.img.setImageResource(book.image)
        holder.title.text = book.title
        holder.author.text = book.author
        holder.synopsis.text = book.synopsis

        holder.itemView.setOnClickListener {

            val context = holder.itemView.context
            val intent = Intent(context, BookDetailsActivity::class.java)

            intent.putExtra("tituloLivro",book.title)
            context.startActivity(intent)
        }



    }

    override fun getItemCount(): Int {
        return cardBooks.size
    }





}