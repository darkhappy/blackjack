package com.example.blackjack

data class Card (
    val rank: String,
    val suit: String,
    var hidden: Boolean = false
)