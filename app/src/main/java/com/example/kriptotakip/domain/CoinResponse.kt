package com.example.kriptotakip.domain

data class CoinResponse(
    val symbol: String,
    val lastPrice: String,          // Ana fiyat
    val priceChangePercent: String, // Yüzde değişimi
    val highPrice: String,          // Günün zirvesi
    val lowPrice: String            // Günün dibi
)
