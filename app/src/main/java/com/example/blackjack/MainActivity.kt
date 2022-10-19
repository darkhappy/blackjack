package com.example.blackjack

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
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
        val list = binding.player
        val dealer = binding.dealer

        viewModel.getPlayerHand().observe(this) { hand ->
            list.removeAllViews()
            for (card in hand) {
                list.addView(displayCard(card))
            }
        }

        viewModel.getDealerHand().observe(this) { hand ->
            dealer.removeAllViews()
            for (card in hand) {
                dealer.addView(displayCard(card))
            }
        }

        startGame()

        hitme.setOnClickListener {
            viewModel.getCard().observe(this) { card ->
                viewModel.addCardToPlayerHand(card)
            }
        }
    }

    fun startGame() {
        viewModel.resetHands()

        viewModel.getCard().observe(this) { card ->
            viewModel.addCardToPlayerHand(card)
            viewModel.getCard().observe(this@MainActivity) { dealerCard ->
                dealerCard.hidden = true
                viewModel.addCardToDealerHand(dealerCard)
                viewModel.getCard().observe(this@MainActivity) { card2 ->
                    viewModel.addCardToPlayerHand(card2)
                    viewModel.getCard().observe(this@MainActivity) { dealerCard2 ->
                        viewModel.addCardToDealerHand(dealerCard2)
                    }
                }
            }
        }
    }

    fun displayCard(card: Card): ImageView {
        val image = ImageView(this)

        // Find the image in the drawable folder
        // The card has the suit and value, so we need to convert it to the correct image name
        if (card.hidden) {
            image.setImageResource(R.drawable.card_back)
        } else {
            val imageName = card.suit.lowercase() + "_" + card.rank.lowercase()
            val id = resources.getIdentifier(imageName, "drawable", packageName)
            image.setImageResource(id)
        }

        image.layoutParams = ViewGroup.LayoutParams(200, 300)

        return image
    }
}