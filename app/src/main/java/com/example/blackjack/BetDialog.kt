package com.example.blackjack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels

class BetDialog : DialogFragment() {
    private val viewModel : MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bet_dialog, container, false)

        view.findViewById<View>(R.id.bet_dialog_button).setOnClickListener {
            onBetClick(view)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMoney().observe(this) {
            view.findViewById<TextView>(R.id.bet_dialog_balance).text = "Balance: $it"
        }
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