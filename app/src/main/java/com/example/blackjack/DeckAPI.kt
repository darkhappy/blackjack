package com.example.blackjack

import retrofit2.http.GET

interface DeckAPI {
    @GET("/getDeck")
    suspend fun getDeck(): Deck
}