package com.lithub.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lithub.app.R
import com.lithub.app.dataclass.Review

class ReviewListAdapter(val reviews: MutableList<Review>) : RecyclerView.Adapter<ReviewListAdapter.ReviewListViewHolder>()
{
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ReviewListViewHolder {

        val view = LayoutInflater.from(p0.context).inflate(R.layout.item_review,p0,false)

            return ReviewListViewHolder(view)


    }

    override fun onBindViewHolder(holder: ReviewListViewHolder, position: Int) {

        val review = reviews[position]

        holder.image.setImageResource(review.image)
        holder.name.text = review.name
        holder.comment.text = review.comment
        holder.score.text = "Nota: ${review.score}"
        
        if (review.book_name.isNotEmpty()) {
            holder.book_name.text = review.book_name
            holder.book_name.visibility = View.VISIBLE
        } else {
            holder.book_name.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    class ReviewListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val image = itemView.findViewById<ImageView>(R.id.usuarioFoto)
        val name = itemView.findViewById<TextView>(R.id.usuarioNome)
        val comment = itemView.findViewById<TextView>(R.id.usuarioComentario)
        val book_name = itemView.findViewById<TextView>(R.id.livroNome)
        val score = itemView.findViewById<TextView>(R.id.usuarioNota)

    }


}