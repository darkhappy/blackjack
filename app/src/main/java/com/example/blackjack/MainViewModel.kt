package com.example.blackjack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
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

    fun resetHands() {
        playerHand.value = mutableListOf()
        dealerHand.value = mutableListOf()
    }
}