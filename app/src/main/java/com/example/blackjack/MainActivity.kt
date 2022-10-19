package com.example.blackjack

import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.blackjack.databinding.ActivityMainBinding
import org.w3c.dom.Text
import java.util.*

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

        hitme.setOnClickListener {
            viewModel.getCard().observe(this) { card ->
                viewModel.addCardToPlayerHand(card)
            }
        }

        viewModel.getPlayerHand().observe(this) { hand ->
            list.removeAllViews()
            for (card in hand) {
                list.addView(displayCard(card))
            }
        }
    }

    fun displayCard(card: Card) : ImageView {
        val image = ImageView(this)

        // Find the image in the drawable folder
        // The card has the suit and value, so we need to convert it to the correct image name
        val imageName = card.suit.lowercase() + "_" + card.rank.lowercase()
        val id = resources.getIdentifier(imageName, "drawable", packageName)
        image.setImageResource(id)
        image.layoutParams = ViewGroup.LayoutParams(200, 300)


        return image
    }
}