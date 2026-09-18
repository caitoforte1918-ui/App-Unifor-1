package com.lithub.app.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lithub.app.R
import com.lithub.app.admin.AdminStudentProfileActivity
import com.lithub.app.dataclass.Student

class StudentListAdapter(val students: List<Student>) : RecyclerView.Adapter<StudentListAdapter.StudentViewHolder>() {

    class StudentViewHolder(view: android.view.View): RecyclerView.ViewHolder(view){

        val image = view.findViewById<ImageView>(R.id.studentPhoto)
        val name = view.findViewById<TextView>(R.id.studentName)
        val course = view.findViewById<TextView>(R.id.studentCourse)



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {

        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.item_student,parent,false)

        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {

        val student = students[position]

        holder.image.setImageResource(student.image)
        holder.name.text =  student.name
        holder.course.text = student.course


        holder.itemView.setOnClickListener {

            val context = holder.itemView.context
            val intent = Intent(context, AdminStudentProfileActivity::class.java)
            intent.putExtra("nome",student.name)
            context.startActivity(intent)

        }


    }

    override fun getItemCount(): Int {
           return students.size

    }


}