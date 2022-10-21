package com.example.blackjack

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers

class MainViewModel : ViewModel() {
    private val repository: Repository = Repository()
    private var deckID : Int = 0

    private var playerHand = MutableLiveData<List<Card>>()
    private var dealerHand = MutableLiveData<List<Card>>()

    init {
        playerHand.value = mutableListOf()
        dealerHand.value = mutableListOf()
    }

    fun getCard() = liveData(Dispatchers.IO) {
        if (deckID == 0) {
            val response = repository.getDeck()
            deckID = response.deckId
        }
        val response = repository.getCard(deckID)
        emit(response)
    }

    fun addCardToPlayerHand(card: Card) {
        val newHand = playerHand.value?.toList()?.plus(card)!!
        playerHand.value = newHand
    }

    fun addCardToDealerHand(card: Card) {
        val newHand = dealerHand.value?.toList()?.plus(card)!!
        dealerHand.value = newHand
    }

    fun getPlayerHand() : LiveData<List<Card>> {
        return playerHand
    }

    fun getDealerHand() : LiveData<List<Card>> {
        return dealerHand
    }

    fun getPlayerHandValue() : Int {
        return getHandValue(playerHand.value!!)
    }

    fun getDealerHandValue() : Int {
        return getHandValue(dealerHand.value!!)
    }

    private fun getHandValue(hand: List<Card>) : Int {
        var value = 0
        hand.forEach { card ->
            value += when (card.rank) {
                "As" -> 11
                "Roi", "Reine", "Valet" -> 10
                else -> card.rank.toInt()
            }
        }

        hand.forEach { card ->
            if (card.rank == "As" && value > 21) {
                value -= 10
            }
        }

        return value
    }

    fun resetHands() {
        playerHand.value = mutableListOf()
        dealerHand.value = mutableListOf()
    }

    fun showDealerHand() {
        // first card is hidden so we need to show it
        val newHand = dealerHand.value?.toMutableList()
        newHand?.set(0, newHand[0].copy(hidden = false))
        dealerHand.value = newHand!!
    }

    fun getDeck() = liveData(Dispatchers.IO) {
        val response = repository.getDeck()
        emit(response)
    }
}