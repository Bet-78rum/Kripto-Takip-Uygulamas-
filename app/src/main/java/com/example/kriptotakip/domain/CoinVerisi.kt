package com.example.kriptotakip.domain

data class CoinVerisi(
    val symbol: String, // Örn: BTCUSDT
    val price: String   // Örn: 52000.00
)


data class CoinYaniti(
    val symbol: String,
    val lastPrice: String,          // Ana fiyat
    val priceChangePercent: String, // Yüzde değişimi
    val highPrice: String,          // Günün zirvesi
    val lowPrice: String            // Günün dibi
)