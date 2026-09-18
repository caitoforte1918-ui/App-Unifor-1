package com.lithub.app.dataclass

data class Request(
    val id: String,
    val image: Int,
    val titulo: String,
    val autor: String,
    val estudante: String,
    val matricula: String,
    val tipo: String,
    val status: String,
    val estudanteUid: String
)