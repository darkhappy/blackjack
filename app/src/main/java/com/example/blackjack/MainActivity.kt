package com.example.blackjack

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.blackjack.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val hitme = binding.hitme
        val deckbutton = binding.deck
        val list = binding.layout

        deckbutton.setOnClickListener {
            viewModel.getDeck.observe(this) {}
        }

        hitme.setOnClickListener {
            viewModel.getCard.observe(this) { card ->
                viewModel.addCardToPlayerHand(card)
            }
        }

        viewModel.getPlayerHand().observe(this) { hand ->
            list.removeAllViews()
            for (card in hand) {
                val cardView = TextView(this)
                cardView.text = card.toString()
                list.addView(cardView)
            }
        }
    }
}