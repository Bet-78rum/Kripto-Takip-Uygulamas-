package com.betul.kriptotakip.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
// güncel kripto fiyatları
object RetrofitClient {
    private const val BASE_URL = "https://api.binance.com/"

    val api: BinanceApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BinanceApi::class.java)
    }
}