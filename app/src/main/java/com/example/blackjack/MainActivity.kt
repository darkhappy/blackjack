package com.example.blackjack

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.blackjack.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var statsViewModel: StatsViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        statsViewModel = ViewModelProvider(this)[StatsViewModel::class.java]

        // Lists
        val playerHand = binding.player
        val dealerHand = binding.dealer
        val splitHand = binding.splitHand
        val statsList = binding.stats

        // Buttons
        val hitme = binding.hitme
        val stand = binding.stand
        val split = binding.split
        val reset = binding.reset

        hitme.isEnabled = false
        stand.isEnabled = false
        split.isEnabled = false

        // Scores
        val playerScore = binding.playerScore
        val dealerScore = binding.dealerScore
        val splitScore = binding.splitScore

        // Show the cards of the player
        viewModel.getPlayerHand().observe(this) { hand ->
            playerHand.removeAllViews()
            for (card in hand) {
                playerHand.addView(displayCard(card))
            }
            playerScore.text = "Player: ${viewModel.getPlayerHandValue()}"
        }

        // Show the cards of the dealer
        viewModel.getDealerHand().observe(this) { hand ->
            dealerHand.removeAllViews()
            for (card in hand) {
                dealerHand.addView(displayCard(card))
            }
            if (hand.isNotEmpty() && !hand.first().hidden) {
                dealerScore.text = "Dealer: ${viewModel.getDealerHandValue()}"
            }
        }

        // Show the cards of the split
        viewModel.getSplitHand().observe(this) { hand ->
            splitHand.removeAllViews()
            for (card in hand) {
                splitHand.addView(displayCard(card))
            }
            splitScore.text = "Split: ${viewModel.getSplitHandValue()}"
        }

        // Show the stats
        statsViewModel.getAll().observe(this) { stats ->
            statsList.removeAllViews()
            val total = stats.sumOf(CardStat::count).toDouble()
            for (stat in stats) {
                var textView = TextView(this)
                textView.text = "${stat.card} - ${stat.count} - ${
                    String.format(
                        "%.2f",
                        stat.count / total * 100
                    )
                }%"
                statsList.addView(textView)
            }
        }

        // Button handlers
        hitme.setOnClickListener {
            handleHit()
        }

        stand.setOnClickListener {
            handleStand()
        }

        reset.setOnClickListener {
            askBet()
            playerScore.text = "Player: 0"
            dealerScore.text = "Dealer: 0"
            splitScore.text = "Split: 0"
        }

        split.setOnClickListener {
            handleSplit()
        }

        // Get a new deck
        viewModel.getDeck().observe(this) {}
        statsViewModel.initialise()

        // Start the game
        askBet()
    }

    // Handles a hit
    fun handleHit() {
        val hitme = binding.hitme
        val stand = binding.stand

        hitme.isEnabled = false
        stand.isEnabled = false

        viewModel.getCard().observe(this) { card ->
            viewModel.addCardToPlayerHand(card)
            statsViewModel.decrease(card)
            hitme.isEnabled = true
            stand.isEnabled = true
            handleBust()
        }
    }

    // Handles a split
    fun handleSplit() {
        // Make sure that the player has the money to split
        if (viewModel.getBet() * 2 > viewModel.getMoney().value!!) {
            Toast.makeText(this, "You don't have enough money to split", Toast.LENGTH_SHORT).show()
            binding.split.isEnabled = false
            return
        }

        viewModel.split()
        binding.split.isEnabled = false
    }

    // Handles a bust from the player/split
    fun handleBust() {
        val playerScore = viewModel.getPlayerHandValue()
        val splitScore = viewModel.getSplitHandValue()

        if (playerScore > 21) {
            // Player hand busted
            Toast.makeText(this, "Player busts!", Toast.LENGTH_SHORT).show()
            viewModel.showDealerHand()
            viewModel.lose()
        } else if (splitScore > 21) {
            // Split hand busted, so switch to the player hand
            Toast.makeText(this, "Split busted, going back to player hand", Toast.LENGTH_SHORT)
                .show()
            viewModel.switchToPlayerHand()
            return
        }

        endGame()
    }

    // Handles a stand from the player/split
    fun handleStand() {
        val hitme = binding.hitme
        val stand = binding.stand

        hitme.isEnabled = false
        stand.isEnabled = false

        if (viewModel.isSplitHandActive()) {
            // Player standed with hand, switch to player hand
            viewModel.switchToPlayerHand()
            Toast.makeText(this, "Switching to player hand", Toast.LENGTH_SHORT).show()
            hitme.isEnabled = true
            stand.isEnabled = true
            return
        }

        // Reveal the dealer's card
        viewModel.showDealerHand()
        if (viewModel.getDealerHandValue() < 17) {
            // Get cards until the dealer has 17 or more
            viewModel.getCard().observe(this) { card ->
                viewModel.addCardToDealerHand(card)
                statsViewModel.decrease(card)
                handleStand()
            }
        } else {
            val playerScore = viewModel.getPlayerHandValue()
            val dealerScore = viewModel.getDealerHandValue()
            val splitScore = viewModel.getSplitHandValue()

            // Verify if the player won
            if (playerScore > dealerScore || dealerScore > 21) {
                Toast.makeText(this, "Player wins!", Toast.LENGTH_SHORT).show()
                viewModel.win()
            } else if (playerScore < dealerScore) {
                Toast.makeText(this, "Dealer wins!", Toast.LENGTH_SHORT).show()
                viewModel.lose()
            } else {
                Toast.makeText(this, "It's a tie!", Toast.LENGTH_SHORT).show()
            }

            // Verify if the split won
            if (splitScore != 0 && splitScore <= 21) {
                if (splitScore > dealerScore || dealerScore > 21) {
                    Toast.makeText(this, "Player wins!", Toast.LENGTH_SHORT).show()
                    viewModel.win()
                } else if (splitScore < dealerScore) {
                    Toast.makeText(this, "Dealer wins!", Toast.LENGTH_SHORT).show()
                    viewModel.lose()
                } else {
                    Toast.makeText(this, "It's a tie!", Toast.LENGTH_SHORT).show()
                }
            }

            endGame()
        }
    }

    // Ask the player for a bet
    fun askBet() {
        val dialog = BetDialog.newInstance();
        dialog.show(supportFragmentManager, "BetDialog")
    }

    // When we receive the bet, start the game
    fun onBet(bet: Int) {
        val playerMoney = viewModel.getMoney().value
        if (bet <= 0) {
            // Make sure the player bets something
            Toast.makeText(this, "You must bet something!", Toast.LENGTH_SHORT).show()
        } else if (playerMoney != null) {
            if (playerMoney < bet) {
                // Can't bet too much
                Toast.makeText(this, "You don't have enough money!", Toast.LENGTH_SHORT).show()
                askBet()
            } else {
                // Start the game
                viewModel.setBet(bet)
                startGame()
            }
        }
    }

    fun startGame() {
        viewModel.resetHands()

        // Initialize the game
        viewModel.getCard().observe(this) { card ->
            viewModel.addCardToPlayerHand(card)
            statsViewModel.decrease(card)

            viewModel.getCard().observe(this@MainActivity) { dealerCard ->
                dealerCard.hidden = true
                viewModel.addCardToDealerHand(dealerCard)
                statsViewModel.decrease(dealerCard)

                viewModel.getCard().observe(this@MainActivity) { card2 ->
                    viewModel.addCardToPlayerHand(card2)
                    statsViewModel.decrease(card2)

                    viewModel.getCard().observe(this@MainActivity) { dealerCard2 ->
                        viewModel.addCardToDealerHand(dealerCard2)
                        statsViewModel.decrease(dealerCard2)

                        val playerScore = viewModel.getPlayerHandValue()
                        val dealerScore = viewModel.getDealerHandValue()

                        // Check for blackjack
                        handleBlackjack(playerScore, dealerScore)

                        // Check for split
                        if (card.rank == card2.rank) {
                            binding.split.isEnabled = true
                        }

                        // Enable the buttons
                        binding.hitme.isEnabled = true
                        binding.stand.isEnabled = true
                        binding.reset.isEnabled = false
                    }
                }
            }
        }
    }

    // Deal with blackjack
    fun handleBlackjack(player: Int, dealer: Int) {
        if (player != 21 && dealer != 21) {
            // No blackjack
            return
        } else if (player == 21 && dealer == 21) {
            // Both have blackjack
            Toast.makeText(this, "It's a tie!", Toast.LENGTH_SHORT).show()
        } else if (player == 21) {
            // Player has blackjack
            Toast.makeText(this, "Player blackjack!", Toast.LENGTH_SHORT).show()
            viewModel.win()
        } else {
            // Dealer has blackjack
            Toast.makeText(this, "Dealer blackjack!", Toast.LENGTH_SHORT).show()
            viewModel.lose()
        }

        // Reveal the dealer's card
        viewModel.showDealerHand()
        endGame()
    }

    fun endGame() {
        // Update the score and disable buttons
        val playerScore = binding.playerScore
        val dealerScore = binding.dealerScore
        playerScore.text = "Player: ${viewModel.getPlayerHandValue()}"
        dealerScore.text = "Dealer: ${viewModel.getDealerHandValue()}"

        binding.hitme.isEnabled = false
        binding.stand.isEnabled = false
        binding.reset.isEnabled = true
    }

    // Display cards
    fun displayCard(card: Card): ImageView {
        val image = ImageView(this)

        if (card.hidden) {
            // Don't display if it's hidden
            image.setImageResource(R.drawable.card_back)
        } else {
            // Find the card and display it
            val imageName = card.suit.lowercase() + "_" + card.rank.lowercase()
            val id = resources.getIdentifier(imageName, "drawable", packageName)
            image.setImageResource(id)
        }

        image.layoutParams = ViewGroup.LayoutParams(200, 300)

        return image
    }
}
