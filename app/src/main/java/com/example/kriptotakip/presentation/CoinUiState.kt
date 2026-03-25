package com.betul.kriptotakip.presentation

import com.betul.kriptotakip.domain.CoinResponse

data class CoinUiState(
    val coins: List<CoinResponse> = emptyList(),
    val prices: Map<String, Double> = emptyMap(),
    val previousPrices: Map<String, Double> = emptyMap(),
    val percentages: Map<String, String> = emptyMap(),
    val favoriteList: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val isDarkMode: Boolean = false,
    val profileColor: Long = 0xFF1ABC9C // Varsayılan renk (Turkuaz)
)
