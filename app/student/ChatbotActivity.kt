package com.lithub.app.student

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.lithub.app.BuildConfig
import com.lithub.app.R
import com.lithub.app.adapter.ChatMessageAdapter
import com.lithub.app.dataclass.Message
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class ChatbotActivity : AppCompatActivity() {
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ChatMessageAdapter
    private lateinit var recycler: RecyclerView

    // Aumento do tempo de tolerância para 60 segundos para evitar Timeout com a IA
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()
    private val apiKey = BuildConfig.GEMINI_API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        recycler = findViewById(R.id.recyclerMessages)
        val input = findViewById<EditText>(R.id.etMessage)
        val send = findViewById<ImageButton>(R.id.btnSend)

        adapter = ChatMessageAdapter(this, messages)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Mensagem inicial ajustada para o sotaque da persona
        if (messages.isEmpty()) {
            addBotMessage("Oxente, olá! Sou Raquel, a assistente virtual do Lit Hub. Como posso ajudar você hoje, meu bem?")
        }

        send.setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isNotEmpty()) {
                addUserMessage(text)
                input.text.clear()
                getResponseFromAI(text)
            }
        }
        setupBottomNavigation()
    }

    private fun getResponseFromAI(userText: String) {
        val model = "gemini-2.5-flash"
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

        // A mágica da persona acontece aqui
        val systemPrompt = """
            Seu nome é Raquel e você é uma assistente Virtual do Lit Hub, mas deve agir com a persona de uma bibliotecária nordestina muito acolhedora, simpática, prestativa e "sabida".
            
            Regras da sua personalidade:
            1. Seja muito educada e use o calor humano típico do Nordeste do Brasil.
            2. Use expressões regionais nordestinas de forma natural e carinhosa (como "oxente", "visse", "meu bem", "arretado", "cheiro", "pois não"), mas sem virar uma caricatura exagerada.
            3. Ajude com livros, regras de aluguel e solicitações da biblioteca sempre com boa vontade.
            4. Você é uma IA versátil, então se o usuário quiser conversar sobre política, receitas, tecnologia ou qualquer outro assunto, converse normalmente, mas mantendo o sotaque e o jeito nordestino.
            5. Mantenha suas respostas curtas, pois o usuário está lendo na tela do celular.
            6. Se não souber de algo específico da biblioteca, diga algo como: "Vixe, meu bem, essa informação eu não tenho aqui, mas procure a administração, visse?"
        """.trimIndent()

        val jsonRequest = JsonObject().apply {
            val contents = JsonArray()
            val userContent = JsonObject().apply {
                addProperty("role", "user")
                val parts = JsonArray()
                parts.add(JsonObject().apply {
                    addProperty("text", "$systemPrompt\n\nUsuário: $userText")
                })
                add("parts", parts)
            }
            contents.add(userContent)
            add("contents", contents)
        }

        val body = gson.toJson(jsonRequest).toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(url).post(body).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API_ERROR", "Falha na rede: ${e.message}")
                addBotMessage("Vixe, deu um erro de rede. A conexão falhou ou demorou demais, visse?")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    try {
                        val aiResponse = parseGeminiResponse(responseBody)
                        addBotMessage(aiResponse)
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Erro ao fazer parse do JSON: ${e.message}")
                        addBotMessage("Oxente, deu um erro aqui pra entender a resposta.")
                    }
                } else {
                    Log.e("API_ERROR", "Código de Erro HTTP: ${response.code} | Resposta: $responseBody")
                    addBotMessage("Erro ${response.code}: Deu um probleminha, meu bem. Verifique o Logcat.")
                }
            }
        })
    }

    private fun parseGeminiResponse(json: String): String {
        val root = gson.fromJson(json, JsonObject::class.java)
        return root.getAsJsonArray("candidates").get(0).asJsonObject
            .getAsJsonObject("content").getAsJsonArray("parts").get(0).asJsonObject
            .get("text").asString
    }

    private fun addUserMessage(text: String) {
        messages.add(Message(text, true))
        adapter.notifyItemInserted(messages.size - 1)
        recycler.scrollToPosition(messages.size - 1)
    }

    private fun addBotMessage(text: String) {
        runOnUiThread {
            messages.add(Message(text, false))
            adapter.notifyItemInserted(messages.size - 1)
            recycler.scrollToPosition(messages.size - 1)
        }
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_chat
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> { startActivity(Intent(this, HomeActivity::class.java)); finish() }
                R.id.nav_books -> { startActivity(Intent(this, MyBooksActivity::class.java)); finish() }
                R.id.nav_notifications -> { startActivity(Intent(this, NotificationsActivity::class.java)); finish() }
                R.id.nav_profile -> { startActivity(Intent(this, ProfileActivity::class.java)); finish() }
            }
            true
        }
    }
}