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

class AdminCardBookListAdapter(private val cardBooks: List<CardBook>): RecyclerView.Adapter<AdminCardBookListAdapter.AdminCardBookViewHolder>(){

    class AdminCardBookViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val img = view.findViewById<ImageView>(R.id.imgBook)
        val title = view.findViewById<TextView>(R.id.txtTitle)
        val author = view.findViewById<TextView>(R.id.txtAuthor)
        val synopsis = view.findViewById<TextView>(R.id.txtSynopsis)




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminCardBookViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.item_cardbook,parent,false)

        return AdminCardBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminCardBookViewHolder, position: Int) {

        val book = cardBooks[position]

        holder.img.setImageResource(book.image)
        holder.title.text = book.title
        holder.author.text = book.author
        holder.synopsis.text = book.synopsis

        val context = holder.itemView.context

        holder.itemView.setOnClickListener {
            val intent = Intent(context, AdminBookDetailActivity::class.java)
            intent.putExtra("tituloAdminCardBookListAdapter",holder.title.text)
            intent.putExtra("autorAdminCardBookListAdapter",holder.author.text)
            context.startActivity(intent)
        }



    }

    override fun getItemCount(): Int {
        return cardBooks.size
    }





}