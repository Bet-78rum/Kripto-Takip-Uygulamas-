package com.betul.kriptotakip.ui.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    symbol: String,
    price: Double,
    percent: String,
    history: List<Double>,
    onBack: () -> Unit
) {
    val isPositive = !percent.startsWith("-")
    val mainColor = if (isPositive) Color(0xFF4CAF50) else Color.Red

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(symbol, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Fiyat Kartı
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Current Price", style = MaterialTheme.typography.labelLarge)
                    Text(
                        text = "$price $",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = mainColor
                    )
                    Surface(
                        color = mainColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "% $percent",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = mainColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Detaylı Grafik Alanı
            Text(
                text = "Live Market Movement",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                if (history.size > 1) {
                    DetailedChart(history = history, color = mainColor)
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Waiting for live data updates...", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // İstatistikler
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("High", "+2.1%", Color(0xFF4CAF50))
                StatItem("Low", "-1.4%", Color.Red)
                StatItem("Vol", "2.4M", Color.Gray)
            }
        }
    }
}

@Composable
fun DetailedChart(history: List<Double>, color: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val minPrice = history.minOrNull() ?: 0.0
        val maxPrice = history.maxOrNull() ?: 1.0
        val priceRange = (maxPrice - minPrice).coerceAtLeast(0.000001)

        val path = Path()
        val fillPath = Path()

        val xStep = width / (history.size - 1).coerceAtLeast(1)

        history.forEachIndexed { index, price ->
            val x = index * xStep
            val normalizedY = (price - minPrice) / priceRange
            val y = height - (normalizedY.toFloat() * height)

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, height)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
            
            if (index == history.size - 1) {
                fillPath.lineTo(x, height)
                fillPath.close()
            }
        }

        // Alanı doldur (Gradient)
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(color.copy(alpha = 0.4f), Color.Transparent)
            )
        )

        // Çizgiyi çiz
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Bold, color = color)
    }
}
