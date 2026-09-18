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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.student.BookDetailsActivity
import com.lithub.app.dataclass.CardBook
import com.lithub.app.R

class CardBookListAdapter(private val cardBooks: List<CardBook>): RecyclerView.Adapter<CardBookListAdapter.CardBookViewHolder>(){

    class CardBookViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val img = view.findViewById<ImageView>(R.id.imgBook)
        val title = view.findViewById<TextView>(R.id.txtTitle)
        val author = view.findViewById<TextView>(R.id.txtAuthor)
        val synopsis = view.findViewById<TextView>(R.id.txtSynopsis)

        val btnLayout = view.findViewById<LinearLayout>(R.id.buttonsLayout)

        val btnDetails = view.findViewById<Button>(R.id.btnDetails)

        val btnRent = view.findViewById<Button>(R.id.btnRent)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardBookViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.item_cardbook,parent,false)

        return CardBookViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardBookViewHolder, position: Int) {

        val book = cardBooks[position]

        holder.img.setImageResource(book.image)
        holder.title.text = book.title
        holder.author.text = book.author
        holder.synopsis.text = book.synopsis

        holder.itemView.setOnClickListener {
            if(holder.btnLayout.isGone){
                holder.btnLayout.visibility = View.VISIBLE
            } else {
                holder.btnLayout.visibility = View.GONE
            }
        }

        holder.btnDetails.setOnClickListener {

            val context = holder.itemView.context
            val intent = Intent(context, BookDetailsActivity::class.java)

            intent.putExtra("tituloLivro",book.title)
            context.startActivity(intent)
        }

        holder.btnRent.setOnClickListener {
            val context = holder.itemView.context
            rentBookDialog(context, book)

        }


    }

    override fun getItemCount(): Int {
        return cardBooks.size
    }

    //Função para criar a tela de aluguel de livros
    fun rentBookDialog(context: Context, book: CardBook){

        //Configuração do Dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rental_books, null)
        val spinner = dialogView.findViewById<Spinner>(R.id.spinnerDays)
        val confirmBtn = dialogView.findViewById<Button>(R.id.confirmBtn)
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancelBtn)

        //Display das informações no Dialog
        val bookTitle = dialogView.findViewById<TextView>(R.id.book_title)
        val bookAuthor = dialogView.findViewById<TextView>(R.id.book_author)
        val imgRentBook = dialogView.findViewById<ImageView>(R.id.imgRentBook)

        //Altera as informações de acordo com o livro escolhido
        bookTitle.text = book.title
        bookAuthor.text = book.author
        imgRentBook.setImageResource(book.image)




        val options = listOf(
            "3 Dias",
            "5 Dias",
            "7 Dias",
            "14 Dias"
        )

        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            options
        )

        spinner.adapter = adapter

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        confirmBtn.setOnClickListener {
            val selectedTime = spinner.selectedItem.toString()
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()
            val uid = auth.currentUser?.uid

            if (uid != null) {
                // Busca as informações do usuário logado
                db.collection("estudantes").document(uid).get()
                    .addOnSuccessListener { document ->
                        val userName = document.getString("nome") ?: "Usuário Desconhecido"
                        val matricula = document.getString("matricula") ?: "Sem Matrícula"

                        val rentalData = hashMapOf(
                            "titulo" to book.title,
                            "autor_livro" to book.author,
                            "estudante_uid" to uid,
                            "tempo" to selectedTime,
                            "status" to "Em análise",
                            "matricula" to matricula,
                            "nome_estudante" to userName,
                            "tipo" to "Livro"
                        )

                        // Adiciona a solicitação com um ID aleatório
                        db.collection("solicitacoes").add(rentalData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Solicitação enviada com sucesso!", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Erro ao processar solicitação", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Erro ao obter dados do perfil", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            }
        }
        cancelBtn.setOnClickListener { dialog.dismiss() }



        dialog.show()



    }



}