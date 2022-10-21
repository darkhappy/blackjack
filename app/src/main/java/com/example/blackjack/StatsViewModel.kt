package com.example.blackjack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StatsViewModel(application: Application) : AndroidViewModel(application) {
    private val statsDAO = CardStatsDatabase.getDatabase(application).cardStatsDao()
    private val stats: MutableLiveData<List<CardStat>> = MutableLiveData()

    init {
        stats.value = statsDAO.getAll()
    }

    fun initialise() {
        statsDAO.deleteAll()

        statsDAO.insert(CardStat("As", 28))
        statsDAO.insert(CardStat("2", 28))
        statsDAO.insert(CardStat("3", 28))
        statsDAO.insert(CardStat("4", 28))
        statsDAO.insert(CardStat("5", 28))
        statsDAO.insert(CardStat("6", 28))
        statsDAO.insert(CardStat("7", 28))
        statsDAO.insert(CardStat("8", 28))
        statsDAO.insert(CardStat("9", 28))
        statsDAO.insert(CardStat("10", 112))
    }

    fun decrease(card: Card) {
        val name : String = when (card.rank) {
            "As" -> {
                "As"
            }
            "Valet", "Reine", "Roi" -> {
                "10"
            }
            else -> {
                card.rank
            }
        }
        statsDAO.update(name, statsDAO.get(name).count - 1)
        stats.value = statsDAO.getAll()
    }

    fun getAll() : LiveData<List<CardStat>> {
        return stats
    }
}