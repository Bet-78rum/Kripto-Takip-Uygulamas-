package com.example.kriptotakip

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.example.kriptotakip.domain.CoinVerisi


@Composable
fun CoinSatiri(coin: CoinVerisi) {
    // Şimdilik basit bir mantık: Fiyat 60.000'den küçükse kırmızı, büyükse yeşil
    // (İleride bunu 'bir önceki fiyata göre' kıyaslayacak şekilde geliştireceğiz)
    val fiyatSayi = coin.price.toDoubleOrNull() ?: 0.0
    val renk = if (fiyatSayi < 60000.0) Color.Red else Color(0xFF4CAF50)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = coin.symbol,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${coin.price} $",
            color = renk, // İşte burası! Renk artık fiyata göre değişiyor.
            fontWeight = FontWeight.Medium
        )
    }
}