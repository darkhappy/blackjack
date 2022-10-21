package com.example.blackjack

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [CardStat::class], version = 1)
abstract class CardStatsDatabase : RoomDatabase() {
    abstract fun cardStatsDao(): CardStatsDAO

    companion object Database {
        fun getDatabase(context: Context): CardStatsDatabase {
            return Room.databaseBuilder(context, CardStatsDatabase::class.java, "card_stats_db")
                .allowMainThreadQueries()
                .build()
        }
    }
}