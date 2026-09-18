package com.lithub.app.dataclass

data class Review (
    val image: Int,
    val name: String,
    val book_name: String,
    val comment: String,
    val score: Double,
    val matricula: String = ""
)