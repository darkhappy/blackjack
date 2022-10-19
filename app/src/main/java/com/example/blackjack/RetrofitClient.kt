package com.example.blackjack

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        fun init(): DeckAPI {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://c56.drynish.synology.me")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(DeckAPI::class.java)
        }
    }
}