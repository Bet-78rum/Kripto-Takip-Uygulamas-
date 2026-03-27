package com.betul.kriptotakip.data

import com.google.gson.annotations.SerializedName

data class BinanceResponse(
    val stream: String,
    val data: TickerData
)

data class TickerData(
    @SerializedName("s") val symbol: String,
    @SerializedName("c") val lastPrice: String,
    @SerializedName("P") val priceChangePercent: String,
    @SerializedName("v") val volume: String
)