package com.example.blackjack

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import org.w3c.dom.Text

class BetDialog : DialogFragment() {

    val money = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bet_dialog, container, false)
        view.findViewById<View>(R.id.bet_dialog_button).setOnClickListener {
            onBetClick(view)
        }
        // show the money the player has
        view.findViewById<TextView>(R.id.bet_dialog_balance).text = "Balance: $money"

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            BetDialog().apply {
                arguments = Bundle().apply {
                }
            }
    }

    // When the user clicks the "Bet" button, the bet amount is sent to the main activity
    fun onBetClick(view: View) {
        val betAmount = view.findViewById<EditText>(R.id.bet_dialog_input).text.toString().toInt()
        val activity = activity as MainActivity
        activity.onBet(betAmount)
        dismiss()
    }
}