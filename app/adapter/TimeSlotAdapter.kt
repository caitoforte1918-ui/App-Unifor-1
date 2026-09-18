package com.lithub.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lithub.app.R

class TimeSlotAdapter(private val timeSlots: List<String>) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    private var selectedPosition = -1

    fun getSelectedTime(): String? {
        return if (selectedPosition != -1) timeSlots[selectedPosition] else null
    }

    class TimeSlotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnTimeSlot: Button = view.findViewById(R.id.btnTimeSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_time_slot, parent, false)
        return TimeSlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val time = timeSlots[position]
        holder.btnTimeSlot.text = time

        if (selectedPosition == position) {
            holder.btnTimeSlot.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.context, R.color.primary))
            holder.btnTimeSlot.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            holder.btnTimeSlot.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.context, R.color.white))
            holder.btnTimeSlot.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.primary))
        }

        holder.btnTimeSlot.setOnClickListener {
            val previousSelected = selectedPosition
            if (selectedPosition == position) {
                selectedPosition = -1 // Deselect if same is clicked
            } else {
                selectedPosition = position
            }
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
        }
    }

    override fun getItemCount(): Int = timeSlots.size
}
