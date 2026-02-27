package com.example.kriptotakip.presentation

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kriptotakip.data.StorageManager
import com.example.kriptotakip.data.remote.RetrofitClient
import com.example.kriptotakip.domain.CoinResponse
import kotlinx.coroutines.launch

class CoinViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.api
    private val storage = StorageManager(application)

    // Fiyat ve yüzde bilgilerini tutan çekmeceler
    var prices by mutableStateOf(mapOf<String, Double>())
    var percentages by mutableStateOf(mapOf<String, String>())
    var previousPrices by mutableStateOf(mapOf<String, Double>())
    var allCoinsList by mutableStateOf(listOf<CoinResponse>())

    // Favori listesini uygulama açıldığında hafızadan yüklüyoruz
    var favoriteList by mutableStateOf(storage.getFavorites())

    val currentPrice: Double get() = prices["BTCUSDT"] ?: 0.0
    val previousPrice: Double get() = previousPrices["BTCUSDT"] ?: 0.0

    // Bütün popüler coinleri ilk açılışta çeker
    fun fetchAllData() {
        viewModelScope.launch {
            try {
                val response = api.getAllCoins()
                allCoinsList = response
                // toString hatası forEach olarak düzeltildi
                response.forEach { coin ->
                    updatePrice(coin.symbol, coin.lastPrice.toDouble(), coin.priceChangePercent)
                }
            } catch (e: Exception) {
                // Hata yönetimi buraya eklenebilir
            }
        }
    }

    // Favori ekleme/çıkarma ve hafızaya kaydetme mantığı
    fun toggleFavorite(symbol: String) {
        val currentFavorites = favoriteList.toMutableSet()
        
        if (currentFavorites.contains(symbol)) {
            currentFavorites.remove(symbol)
        } else {
            currentFavorites.add(symbol)
        }
        
        // State'i güncelle (UI'ın değişmesi için)
        favoriteList = currentFavorites
        
        // Hafızaya kalıcı olarak kaydet
        storage.saveFavorites(currentFavorites)
    }

    // Soketten gelen canlı verileri işleme
    fun updatePrice(symbol: String, newPrice: Double, newPercent: String) {
        val currentPrice = prices[symbol] ?: 0.0
        
        // Fiyat değiştiyse eski fiyatı kaydet (Renk değişimi için)
        if (newPrice != currentPrice) {
            previousPrices = previousPrices + (symbol to currentPrice)
            prices = prices + (symbol to newPrice)
        }
        percentages = percentages + (symbol to newPercent)
    }
}
