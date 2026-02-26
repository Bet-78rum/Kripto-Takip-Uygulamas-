package com.example.kriptotakip.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.kriptotakip.CoinDetayKarti

@Composable
fun CoinEkranı(viewModel: CoinViewModel) {
    val symbol = "BTCUSDT"

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CoinDetayKarti(
            symbol = "BTC / USDT",
            fiyat = viewModel.currentPrice,
            yuzde = viewModel.yuzdeler[symbol] ?: "0.00",
            eskiFiyat = viewModel.previousPrice,
            isFavori = viewModel.favoriListesi.contains(symbol),
            onFavoriClick = { viewModel.favoriDegistir(symbol) }
        )
    }
}
