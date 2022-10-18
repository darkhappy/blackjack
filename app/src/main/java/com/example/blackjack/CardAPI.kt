package com.example.blackjack

import retrofit2.http.GET
import retrofit2.http.Path

interface CardAPI {
    @GET("/getCard/{id}")
    suspend fun getCard(@Path(value = "id") deckId: Int): Card
}