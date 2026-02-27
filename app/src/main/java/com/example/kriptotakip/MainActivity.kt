package com.example.kriptotakip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kriptotakip.data.BinanceSocketManager
import com.example.kriptotakip.data.StorageManager
import com.example.kriptotakip.presentation.CoinViewModel
import com.example.kriptotakip.ui.theme.KriptoTakipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryptoMainScreen()
        }
    }
}

@Composable
fun CryptoMainScreen() {
    val context = LocalContext.current
    val storage = remember { StorageManager(context) }
    val viewModel: CoinViewModel = viewModel()
    
    // Tema durumunu hafızadan okuyarak başlatıyoruz
    var isDarkMode by remember { mutableStateOf(storage.getTheme()) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Favorites, 1: Popular
    var searchQuery by remember { mutableStateOf("") }
    
    val tabs = listOf("Favorites ⭐", "Popular")
    
    // Uygulamada gösterilecek en popüler 15 coin listesi
    val popularCoins = listOf(
        "BTCUSDT", "ETHUSDT", "BNBUSDT", "SOLUSDT", "XRPUSDT",
        "ADAUSDT", "DOGEUSDT", "TRXUSDT", "DOTUSDT", "LTCUSDT",
        "AVAXUSDT", "SHIBUSDT", "LINKUSDT", "MATICUSDT", "UNIUSDT"
    )

    // Canlı verileri başlatıyoruz
    LaunchedEffect(Unit) {
        viewModel.fetchAllData() // İlk verileri REST API ile çek
        
        // WebSocket ile canlı fiyatları dinlemeye başla
        val socket = BinanceSocketManager { symbol, price, percent ->
            viewModel.updatePrice(symbol, price, percent)
        }
        socket.start()
    }

    KriptoTakipTheme(darkTheme = isDarkMode) {
        Scaffold(
            topBar = {
                Column {
                    // Üst Satır: Başlık ve Gece Modu
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 0.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Live Crypto",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { newValue ->
                                isDarkMode = newValue
                                storage.saveTheme(newValue)
                            }
                        )
                    }

                    // Arama Kutusu
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 8.dp),
                        placeholder = { Text("Search Coin...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )
                    
                    // Sekmeler
                    TabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            // Filtreleme Mantığı
            val filteredList = viewModel.allCoinsList.filter { coin ->
                val tabCondition = if (selectedTab == 1) {
                    popularCoins.contains(coin.symbol)
                } else {
                    viewModel.favoriteList.contains(coin.symbol)
                }
                val searchCondition = coin.symbol.contains(searchQuery, ignoreCase = true)
                tabCondition && searchCondition
            }

            // Boş Liste Uyarıları
            if (filteredList.isEmpty() && selectedTab == 0) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Daha coin eklemediniz!",
                        modifier = Modifier.padding(24.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredList, key = { it.symbol }) { coin ->
                        CoinDetailCard(
                            symbol = coin.symbol,
                            price = viewModel.prices[coin.symbol] ?: 0.0,
                            percent = viewModel.percentages[coin.symbol] ?: "0.00",
                            previousPrice = viewModel.previousPrices[coin.symbol] ?: 0.0,
                            isFavorite = viewModel.favoriteList.contains(coin.symbol),
                            onFavoriteClick = { viewModel.toggleFavorite(coin.symbol) }
                        )
                    }
                }
            }
        }
    }
}
