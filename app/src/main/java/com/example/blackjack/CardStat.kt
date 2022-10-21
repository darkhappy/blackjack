package com.example.blackjack

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CardStat(
    @PrimaryKey val card: String,
    @ColumnInfo(name = "count", defaultValue = "28") val count: Int
)
