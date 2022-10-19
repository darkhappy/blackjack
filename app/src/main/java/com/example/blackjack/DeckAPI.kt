package com.example.blackjack

import retrofit2.http.GET
import retrofit2.http.Path

interface DeckAPI {
    @GET("/getDeck")
    suspend fun getDeck(): Deck

    @GET("/getCard/{id}")
    suspend fun getCard(@Path(value = "id") deckId: Int): Card
}