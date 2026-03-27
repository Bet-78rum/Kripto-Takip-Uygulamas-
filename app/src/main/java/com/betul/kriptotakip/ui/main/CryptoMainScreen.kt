package com.betul.kriptotakip.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.betul.kriptotakip.presentation.CoinViewModel
import com.betul.kriptotakip.ui.components.CoinDetailCard
import com.betul.kriptotakip.ui.theme.KriptoTakipTheme

@Composable
fun CryptoMainScreen(
    onNavigateToProfile: () -> Unit
) {
    val viewModel: CoinViewModel = viewModel()
    val uiState = viewModel.uiState
    
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("Favorites ⭐", "Popular")
    val popularCoins = listOf(
        "BTCUSDT", "ETHUSDT", "BNBUSDT", "SOLUSDT", "XRPUSDT",
        "ADAUSDT", "DOGEUSDT", "TRXUSDT", "DOTUSDT", "LTCUSDT",
        "AVAXUSDT", "SHIBUSDT", "LINKUSDT", "MATICUSDT", "UNIUSDT"
    )

    // Detay sayfası açıksa onu göster
    if (uiState.selectedCoin != null) {
        val symbol = uiState.selectedCoin
        CoinDetailScreen(
            symbol = symbol,
            price = uiState.prices[symbol] ?: 0.0,
            percent = uiState.percentages[symbol] ?: "0.00",
            history = uiState.priceHistory[symbol] ?: emptyList(),
            onBack = { viewModel.selectCoin(null) }
        )
    } else {
        KriptoTakipTheme(darkTheme = uiState.isDarkMode) {
            Scaffold(
                topBar = {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp, top = 40.dp, bottom = 0.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = onNavigateToProfile) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = "Live Crypto",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Switch(
                                checked = uiState.isDarkMode,
                                onCheckedChange = { viewModel.toggleTheme(it) }
                            )
                        }

                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 8.dp),
                            placeholder = { Text("Search Coin...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium
                        )

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
                val filteredList = uiState.coins.filter { coin ->
                    val tabCondition = if (selectedTab == 1) {
                        popularCoins.contains(coin.symbol)
                    } else {
                        uiState.favoriteList.contains(coin.symbol)
                    }
                    val searchCondition = coin.symbol.contains(uiState.searchQuery, ignoreCase = true)
                    tabCondition && searchCondition
                }

                if (filteredList.isEmpty() && selectedTab == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "You haven't added any favorite coins yet!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
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
                                price = uiState.prices[coin.symbol] ?: 0.0,
                                percent = uiState.percentages[coin.symbol] ?: "0.00",
                                previousPrice = uiState.previousPrices[coin.symbol] ?: 0.0,
                                isFavorite = uiState.favoriteList.contains(coin.symbol),
                                onFavoriteClick = { viewModel.toggleFavorite(coin.symbol) },
                                onClick = { viewModel.selectCoin(coin.symbol) }
                            )
                        }
                    }
                }
            }
        }
    }
}
