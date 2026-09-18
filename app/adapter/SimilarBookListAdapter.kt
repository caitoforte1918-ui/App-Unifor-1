package com.lithub.app.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lithub.app.dataclass.Book
import com.lithub.app.R
import com.lithub.app.student.BookDetailsActivity

class SimilarBookListAdapter(private val books: List<Book>) : RecyclerView.Adapter<SimilarBookListAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image = view.findViewById<ImageView>(R.id.imgBook)
        val genre = view.findViewById<TextView>(R.id.txtGenre)
        val title = view.findViewById<TextView>(R.id.txtTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book, parent, false)

        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {

        val book = books[position]

        holder.image.setImageResource(book.image)
        holder.genre.text = book.genre
        holder.title.text = book.title

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, BookDetailsActivity::class.java)
            intent.putExtra("tituloLivro", book.title)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return books.size
    }
}