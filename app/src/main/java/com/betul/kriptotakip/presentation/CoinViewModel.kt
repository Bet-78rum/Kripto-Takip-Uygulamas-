package com.betul.kriptotakip.presentation

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.betul.kriptotakip.data.StorageManager
import com.betul.kriptotakip.repository.CoinRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoinViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CoinRepository(StorageManager(application))
    private val auth = FirebaseAuth.getInstance()

    var uiState by mutableStateOf(CoinUiState())
        private set

    private val popularCoins = listOf(
        "BTCUSDT", "ETHUSDT", "BNBUSDT", "SOLUSDT", "XRPUSDT",
        "ADAUSDT", "DOGEUSDT", "TRXUSDT", "DOTUSDT", "LTCUSDT",
        "AVAXUSDT", "SHIBUSDT", "LINKUSDT", "MATICUSDT", "UNIUSDT"
    )

    init {
        fetchAllData()
    }

    fun fetchAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)
            try {
                val coins = repository.getAllCoins()
                val userData = repository.getUserData()

                launch(Dispatchers.Main) {
                    val currentPrices = mutableMapOf<String, Double>()
                    val currentPercentages = mutableMapOf<String, String>()
                    
                    coins.forEach { coin ->
                        currentPrices[coin.symbol] = coin.lastPrice.toDoubleOrNull() ?: 0.0
                        currentPercentages[coin.symbol] = coin.priceChangePercent
                    }

                    uiState = uiState.copy(
                        coins = coins,
                        favoriteList = userData.first,
                        isDarkMode = userData.second,
                        profileColor = userData.third,
                        prices = currentPrices,
                        percentages = currentPercentages,
                        isLoading = false
                    )
                    startLiveUpdates()
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) { uiState = uiState.copy(isLoading = false) }
            }
        }
    }

    private fun startLiveUpdates() {
        repository.startSocket { symbol, price, percent ->
            // Sadece popüler 15 coin veya favoriler ise işle
            if (popularCoins.contains(symbol) || uiState.favoriteList.contains(symbol)) {
                updatePrice(symbol, price, percent)
            }
        }
    }

    fun updatePrice(symbol: String, newPrice: Double, newPercent: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val currentPrices = uiState.prices.toMutableMap()
            val currentPreviousPrices = uiState.previousPrices.toMutableMap()
            val currentPercentages = uiState.percentages.toMutableMap()
            val currentHistory = uiState.priceHistory.toMutableMap()

            val oldPrice = currentPrices[symbol] ?: 0.0

            if (newPrice != oldPrice) {
                currentPreviousPrices[symbol] = oldPrice
                currentPrices[symbol] = newPrice
                
                // Grafik için sadece o an izlenen coinin geçmişini tut
                if (uiState.selectedCoin == symbol) {
                    val history = currentHistory[symbol]?.toMutableList() ?: mutableListOf()
                    history.add(newPrice)
                    if (history.size > 50) history.removeAt(0)
                    currentHistory[symbol] = history
                }

                uiState = uiState.copy(
                    prices = currentPrices,
                    previousPrices = currentPreviousPrices,
                    percentages = currentPercentages,
                    priceHistory = currentHistory
                )
            } else {
                currentPercentages[symbol] = newPercent
                uiState = uiState.copy(percentages = currentPercentages)
            }
        }
    }

    fun selectCoin(symbol: String?) {
        uiState = uiState.copy(selectedCoin = symbol)
    }

    fun toggleFavorite(symbol: String) {
        val currentFavorites = uiState.favoriteList.toMutableSet()
        if (currentFavorites.contains(symbol)) {
            currentFavorites.remove(symbol)
        } else {
            currentFavorites.add(symbol)
        }
        uiState = uiState.copy(favoriteList = currentFavorites)
        repository.saveUserData(currentFavorites, uiState.isDarkMode, uiState.profileColor)
    }

    fun toggleTheme(isDark: Boolean) {
        uiState = uiState.copy(isDarkMode = isDark)
        repository.saveUserData(uiState.favoriteList, isDark, uiState.profileColor)
    }

    fun updateProfileColor(color: Long) {
        uiState = uiState.copy(profileColor = color)
        repository.saveUserData(uiState.favoriteList, uiState.isDarkMode, color)
    }

    fun updateName(newName: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null && newName.isNotBlank()) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()
                user.updateProfile(profileUpdates)
                repository.updateUserName(newName)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        uiState = uiState.copy(searchQuery = query)
    }
}
