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

        val list = binding.player
        val dealer = binding.dealer
        val statsList = binding.stats

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
            playerScore.text = "Player: ${viewModel.getPlayerHandValue()}"
        }

        viewModel.getDealerHand().observe(this) { hand ->
            dealer.removeAllViews()
            for (card in hand) {
                dealer.addView(displayCard(card))
            }
            if (hand.isNotEmpty() && !hand.first().hidden) {
                dealerScore.text = "Dealer: ${viewModel.getDealerHandValue()}"
            }
        }

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

        viewModel.getDeck().observe(this) {}

        hitme.setOnClickListener {
            handleHit()
        }

        stand.setOnClickListener {
            handleStand()
        }

        reset.setOnClickListener {
            askBet()
            playerScore.text = "Player: 0"
            dealerScore.text = "Player: 0"
        }

        askBet()
    }

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
            if (viewModel.getPlayerHandValue() > 21) {
                handleBust()
            }
        }
    }

    fun handleBust() {
        val playerScore = viewModel.getPlayerHandValue()
        val dealerScore = viewModel.getDealerHandValue()

        if (playerScore > 21) {
            Toast.makeText(this, "Player busts!", Toast.LENGTH_SHORT).show()
            viewModel.showDealerHand()
            viewModel.lose()
        } else if (dealerScore > 21) {
            Toast.makeText(this, "Dealer busts!", Toast.LENGTH_SHORT).show()
            viewModel.win()
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
                statsViewModel.decrease(card)
                handleStand()
            }
        } else if (viewModel.getDealerHandValue() > 21) {
            handleBust()
        } else {
            val playerScore = viewModel.getPlayerHandValue()
            val dealerScore = viewModel.getDealerHandValue()
            if (playerScore > dealerScore) {
                Toast.makeText(this, "Player wins!", Toast.LENGTH_SHORT).show()
                viewModel.win()
            } else if (playerScore < dealerScore) {
                Toast.makeText(this, "Dealer wins!", Toast.LENGTH_SHORT).show()
                viewModel.lose()
            } else {
                Toast.makeText(this, "It's a tie!", Toast.LENGTH_SHORT).show()
            }

            endGame()
        }
    }

    fun askBet() {
        val dialog = BetDialog.newInstance();
        dialog.show(supportFragmentManager, "BetDialog")
    }

    fun onBet(bet: Int) {
        val playerMoney = viewModel.getMoney().value
        if (bet == 0) {
            Toast.makeText(this, "You must bet something!", Toast.LENGTH_SHORT).show()
        } else if (playerMoney != null) {
            if (playerMoney < bet) {
                Toast.makeText(this, "You don't have enough money!", Toast.LENGTH_SHORT).show()
                askBet()
            } else {
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
            viewModel.win()
        } else {
            Toast.makeText(this, "Dealer blackjack!", Toast.LENGTH_SHORT).show()
            viewModel.lose()
        }

        viewModel.showDealerHand()
        endGame()
    }

    fun endGame() {
        val playerScore = binding.playerScore
        val dealerScore = binding.dealerScore
        playerScore.text = "Player: ${viewModel.getPlayerHandValue()}"
        dealerScore.text = "Dealer: ${viewModel.getDealerHandValue()}"

        val hitme = binding.hitme
        val stand = binding.stand

        hitme.isEnabled = false
        stand.isEnabled = false
    }

    fun displayCard(card: Card): ImageView {
        val image = ImageView(this)

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
