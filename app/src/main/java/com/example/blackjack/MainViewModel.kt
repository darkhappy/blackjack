package com.example.blackjack

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

class MainViewModel : ViewModel() {
    private val repository: Repository = Repository()
    var deckID : Int = 258

    private var playerHand : MutableLiveData<List<Card>> = MutableLiveData<List<Card>>()
    private var dealerHand = mutableListOf<Card>()

    init {
        playerHand.value = mutableListOf<Card>()
    }

    fun getDeck() = liveData(Dispatchers.IO) {
        val response = repository.getDeck()
        emit(response)
    }

    fun getCard() = liveData(Dispatchers.IO) {
        val response = repository.getCard(deckID)
        emit(response)
    }

    fun addCardToPlayerHand(card: Card) {
        val newHand = playerHand.value?.toList()?.plus(card)!!
        playerHand.value = newHand
    }

    fun addCardToDealerHand(card: Card) {
        dealerHand.add(card)
    }

    fun getPlayerHand() : LiveData<List<Card>> {
        return playerHand
    }

    fun getDealerHand() : MutableList<Card> {
        return dealerHand
    }

    fun resetHands() {
        dealerHand.clear()
    }
}