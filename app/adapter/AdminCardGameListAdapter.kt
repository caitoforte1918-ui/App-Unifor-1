package com.lithub.app.adapter


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lithub.app.dataclass.Game
import com.lithub.app.R
import com.lithub.app.admin.AdminGameDetailActivity

class AdminCardGameListAdapter(val games: List<Game>): RecyclerView.Adapter<AdminCardGameListAdapter.AdminCardGameListViewHolder>() {

    class AdminCardGameListViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val title = view.findViewById<TextView>(R.id.txtTituloJogo)
        val desc = view.findViewById<TextView>(R.id.txtDescJogo)
        val img = view.findViewById<ImageView>(R.id.imgJogo)

        val rule = view.findViewById<TextView>(R.id.txtRegraJogo)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminCardGameListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cardgame, parent, false)

        return AdminCardGameListViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminCardGameListViewHolder, position: Int) {

        val game = games[position]

        holder.title.text = game.title
        holder.desc.text = game.desc
        holder.img.setImageResource(game.image)
        holder.rule.text = game.rules

        val context = holder.itemView.context

        holder.itemView.setOnClickListener {
            val intent = Intent(context, AdminGameDetailActivity::class.java)
            intent.putExtra("tituloAdminCardGameListAdapter", game.title)


            context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return games.size
    }

}