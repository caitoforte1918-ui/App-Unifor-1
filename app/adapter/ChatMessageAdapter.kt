package com.lithub.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lithub.app.dataclass.Message
import com.lithub.app.R

class ChatMessageAdapter(private val context: Context, private val items: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemViewType(position: Int): Int = if (items[position].sentByMe) 1 else 0
    class Holder(view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layout = if (viewType == 1) R.layout.item_message_sent else R.layout.item_message_received
        return Holder(LayoutInflater.from(context).inflate(layout, parent, false))
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        val id = if (item.sentByMe) R.id.tvMessageSent else R.id.tvMessageReceived
        holder.itemView.findViewById<TextView>(id).text = item.text
    }
    override fun getItemCount() = items.size
}