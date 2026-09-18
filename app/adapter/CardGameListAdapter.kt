package com.lithub.app.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.lithub.app.dataclass.Game
import com.lithub.app.R

class CardGameListAdapter(val games: List<Game>): RecyclerView.Adapter<CardGameListAdapter.CardGameViewHolder>() {

   class CardGameViewHolder(view: View): RecyclerView.ViewHolder(view){

       val title = view.findViewById<TextView>(R.id.txtTituloJogo)
       val desc = view.findViewById<TextView>(R.id.txtDescJogo)
       val img = view.findViewById<ImageView>(R.id.imgJogo)

       val rule = view.findViewById<TextView>(R.id.txtRegraJogo)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardGameViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.item_cardgame,parent,false)

        return CardGameViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardGameViewHolder, position: Int) {

        val game = games[position]

        holder.title.text = game.title
        holder.desc.text = game.desc
        holder.img.setImageResource(game.image)
        holder.rule.text = game.rules

        val context = holder.itemView.context





        holder.itemView.setOnClickListener {
            rentGameDialog(context,game)
        }


    }

    override fun getItemCount(): Int {
        return games.size
    }

    fun rentGameDialog(context: Context, game: Game) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rental_games, null)

        val txtTitle = dialogView.findViewById<TextView>(R.id.txtTitle)
        val btnConfirm = dialogView.findViewById<Button>(R.id.buttonConfirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.buttonCancel)
        val rvTimeSlots = dialogView.findViewById<RecyclerView>(R.id.rvTimeSlots)

        txtTitle.text = "Aluguel - ${game.title}"

        // Lista de horários sugeridos
        val timeSlots = listOf("10h - 11h", "11h - 12h", "12h - 13h", "13h - 14h", "14h - 15h", "15h - 16h")
        val timeAdapter = TimeSlotAdapter(timeSlots)
        rvTimeSlots.layoutManager = GridLayoutManager(context, 2)
        rvTimeSlots.adapter = timeAdapter

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        btnConfirm.setOnClickListener {
            val selectedTime = timeAdapter.getSelectedTime()
            if (selectedTime == null) {
                Toast.makeText(context, "Por favor, selecione um horário", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()
            val uid = auth.currentUser?.uid

            if (uid != null) {
                // Busca o nome do usuário
                db.collection("estudantes").document(uid).get()
                    .addOnSuccessListener { document ->
                        val userName = document.getString("nome") ?: "Usuário Desconhecido"
                        val matricula = document.getString("matricula") ?: "Matrícula Desconhecida"

                        val rentalData = hashMapOf(
                            "horario" to selectedTime,
                            "nome_estudante" to userName,
                            "matricula" to matricula,
                            "status" to "Em análise",
                            "tipo" to "Jogo",
                            "titulo" to game.title,
                            "titulo_lower" to game.title.lowercase()
                        )

                        // Adiciona o documento usando o UID como ID (conforme solicitado)
                        db.collection("solicitacoes").document(uid).set(rentalData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Solicitação de aluguel enviada!", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Erro ao enviar solicitação", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Erro ao buscar dados do usuário", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }


}