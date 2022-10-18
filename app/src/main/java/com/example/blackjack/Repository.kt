package com.example.blackjack

class Repository {
    var deckClient: DeckAPI = RetrofitClient.initDeck()
    var cardClient: CardAPI = RetrofitClient.initCard()

    suspend fun getDeck(): Deck = deckClient.getDeck()

    suspend fun getCard(deckId: Int): Card = cardClient.getCard(deckId)
}