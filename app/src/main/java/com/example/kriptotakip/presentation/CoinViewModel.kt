package com.betul.kriptotakip.presentation

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.betul.kriptotakip.data.StorageManager
import com.betul.kriptotakip.repository.CoinRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch

class CoinViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CoinRepository(StorageManager(application))
    private val auth = FirebaseAuth.getInstance()

    var uiState by mutableStateOf(CoinUiState())
        private set

    init {
        fetchAllData()
        startLiveUpdates()
    }

    fun fetchAllData() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            
            try {
                val coins = repository.getAllCoins()
                val userData = repository.getUserData()

                uiState = uiState.copy(
                    coins = coins,
                    favoriteList = userData.first,
                    isDarkMode = userData.second,
                    profileColor = userData.third,
                    isLoading = false
                )

                coins.forEach { coin ->
                    updatePrice(coin.symbol, coin.lastPrice.toDouble(), coin.priceChangePercent)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    private fun startLiveUpdates() {
        repository.startSocket { symbol, price, percent ->
            updatePrice(symbol, price, percent)
        }
    }

    fun updatePrice(symbol: String, newPrice: Double, newPercent: String) {
        val currentPrices = uiState.prices.toMutableMap()
        val currentPreviousPrices = uiState.previousPrices.toMutableMap()
        val currentPercentages = uiState.percentages.toMutableMap()

        val oldPrice = currentPrices[symbol] ?: 0.0

        if (newPrice != oldPrice) {
            currentPreviousPrices[symbol] = oldPrice
            currentPrices[symbol] = newPrice
        }
        currentPercentages[symbol] = newPercent

        uiState = uiState.copy(
            prices = currentPrices,
            previousPrices = currentPreviousPrices,
            percentages = currentPercentages
        )
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

    // İsim güncelleme fonksiyonu
    fun updateName(newName: String) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null && newName.isNotBlank()) {
                // 1. Firebase Auth Profilini Güncelle
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()
                user.updateProfile(profileUpdates)
                
                // 2. Firestore'u Güncelle
                repository.updateUserName(newName)
                
                // 3. UI'ı tazele (İsim bilgilerini yeniden yükle)
                // Normalde reload() gerekebilir ama biz direkt user nesnesini tazeleyebiliriz
            }
        }
    }

    fun updateSearchQuery(query: String) {
        uiState = uiState.copy(searchQuery = query)
    }
}
