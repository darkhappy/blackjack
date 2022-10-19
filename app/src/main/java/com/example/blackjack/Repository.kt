package com.example.blackjack

class Repository {
    private var client: DeckAPI = RetrofitClient.init()

    suspend fun getDeck(): Deck = client.getDeck()

    suspend fun getCard(deckId: Int): Card = client.getCard(deckId)
}