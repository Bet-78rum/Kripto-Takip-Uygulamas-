package com.betul.kriptotakip.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CoinDetailCard(
    symbol: String,
    price: Double,
    percent: String,
    previousPrice: Double,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit // Tıklama için yeni parametre
) {
    val percentColor = if (percent.startsWith("-")) Color.Red else Color(0xFF4CAF50)
    
    val priceColor = when {
        price > previousPrice && previousPrice != 0.0 -> Color(0xFF4CAF50)
        price < previousPrice && previousPrice != 0.0 -> Color.Red
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Kartın tamamına tıklanabilir yaptık
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFFFD700) else Color.Gray
                    )
                }
                
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = symbol,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "% $percent",
                        color = percentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Text(
                text = "$price $",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = priceColor
            )
        }
    }
}
