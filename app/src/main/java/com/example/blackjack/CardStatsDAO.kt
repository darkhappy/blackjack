package com.example.blackjack

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CardStatsDAO {
    @Query("SELECT * FROM CardStat")
    fun getAll(): List<CardStat>

    @Insert
    fun insert(cardStat: CardStat)

    @Query("UPDATE CardStat SET count = :count WHERE card = :card")
    fun update(card: String, count: Int)

    @Query("DELETE FROM CardStat")
    fun deleteAll()

    @Query("SELECT * FROM CardStat WHERE card = :card")
    fun get(card: String): CardStat
}