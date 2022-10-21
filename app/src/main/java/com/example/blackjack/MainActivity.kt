package com.example.blackjack

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.blackjack.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val list = binding.player
        val dealer = binding.dealer

        val hitme = binding.hitme
        val stand = binding.stand

        hitme.isEnabled = false
        stand.isEnabled = false

        val reset = binding.reset

        val playerScore = binding.playerScore
        val dealerScore = binding.dealerScore

        viewModel.getPlayerHand().observe(this) { hand ->
            list.removeAllViews()
            for (card in hand) {
                list.addView(displayCard(card))
            }
            playerScore.text = viewModel.getPlayerHandValue().toString()
        }

        viewModel.getDealerHand().observe(this) { hand ->
            dealer.removeAllViews()
            for (card in hand) {
                dealer.addView(displayCard(card))
            }
            if (hand.isNotEmpty() && !hand.first().hidden) {
                dealerScore.text = viewModel.getDealerHandValue().toString()
            }
        }

        startGame()

        hitme.setOnClickListener {
            handleHit()
        }

        stand.setOnClickListener {
            handleStand()
        }

        reset.setOnClickListener {
            startGame()
            playerScore.text = "0"
            dealerScore.text = "0"
        }
    }

    fun handleHit() {
        val hitme = binding.hitme
        val stand = binding.stand

        hitme.isEnabled = false
        stand.isEnabled = false


        if (viewModel.getPlayerHandValue() < 22) {
            viewModel.getCard().observe(this) { card ->
                viewModel.addCardToPlayerHand(card)
                hitme.isEnabled = true
                stand.isEnabled = true
                if (viewModel.getPlayerHandValue() > 21) {
                    handleBust()
                }
            }
        }
    }

    fun handleBust() {
        val playerScore = viewModel.getPlayerHandValue()
        val dealerScore = viewModel.getDealerHandValue()

        if (playerScore > 21) {
            Toast.makeText(this, "Player busts!", Toast.LENGTH_SHORT).show()
            viewModel.showDealerHand()
        } else if (dealerScore > 21) {
            Toast.makeText(this, "Dealer busts!", Toast.LENGTH_SHORT).show()
        }

        endGame()
    }

    fun handleStand() {
        val hitme = binding.hitme
        val stand = binding.stand

        hitme.isEnabled = false
        stand.isEnabled = false
        viewModel.showDealerHand()
        if (viewModel.getDealerHandValue() < 17) {
            viewModel.getCard().observe(this) { card ->
                viewModel.addCardToDealerHand(card)
                handleStand()
            }
        } else if (viewModel.getDealerHandValue() > 21) {
            handleBust()
        } else {
            val playerScore = viewModel.getPlayerHandValue()
            val dealerScore = viewModel.getDealerHandValue()
            if (playerScore > dealerScore) {
                Toast.makeText(this, "Player wins!", Toast.LENGTH_SHORT).show()
            } else if (playerScore < dealerScore) {
                Toast.makeText(this, "Dealer wins!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "It's a tie!", Toast.LENGTH_SHORT).show()
            }

            endGame()
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

                        val playerScore = viewModel.getPlayerHandValue()
                        val dealerScore = viewModel.getDealerHandValue()

                        handleBlackjack(playerScore, dealerScore)

                        val hitme = binding.hitme
                        val stand = binding.stand

                        hitme.isEnabled = true
                        stand.isEnabled = true
                    }
                }
            }
        }
    }

    fun handleBlackjack(player: Int, dealer: Int) {
        if (player != 21 && dealer != 21) {
            return
        }

        if (player == 21 && dealer == 21) {
            Toast.makeText(this, "It's a tie!", Toast.LENGTH_SHORT).show()
        } else if (player == 21) {
            Toast.makeText(this, "Player blackjack!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Dealer blackjack!", Toast.LENGTH_SHORT).show()
        }

        viewModel.showDealerHand()
        endGame()
    }

    fun endGame() {
        val playerScore = binding.playerScore
        val dealerScore = binding.dealerScore
        playerScore.text = viewModel.getPlayerHandValue().toString()
        dealerScore.text = viewModel.getDealerHandValue().toString()

        val hitme = binding.hitme
        val stand = binding.stand

        hitme.isEnabled = false
        stand.isEnabled = false
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