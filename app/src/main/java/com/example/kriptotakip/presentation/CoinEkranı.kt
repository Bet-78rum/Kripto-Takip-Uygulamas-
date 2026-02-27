package com.example.kriptotakip.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.kriptotakip.CoinDetailCard

@Composable
fun CoinScreen(viewModel: CoinViewModel) {
    val symbol = "BTCUSDT"

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CoinDetailCard(
            symbol = "BTC / USDT",
            price = viewModel.currentPrice,
            percent = viewModel.percentages[symbol] ?: "0.00",
            previousPrice = viewModel.previousPrice,
            isFavorite = viewModel.favoriteList.contains(symbol),
            onFavoriteClick = { viewModel.toggleFavorite(symbol) }
        )
    }
}
