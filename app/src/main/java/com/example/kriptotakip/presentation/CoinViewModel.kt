package com.example.kriptotakip.presentation

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kriptotakip.data.HafizaYoneticisi
import com.example.kriptotakip.data.remote.RetrofitClient
import com.example.kriptotakip.domain.CoinYaniti
import kotlinx.coroutines.launch

class CoinViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitClient.api
    private val hafiza = HafizaYoneticisi(application)

    // Fiyat ve yüzde bilgilerini tutan çekmeceler
    var fiyatlar by mutableStateOf(mapOf<String, Double>())
    var yuzdeler by mutableStateOf(mapOf<String, String>())
    var eskiFiyatlar by mutableStateOf(mapOf<String, Double>())
    var tumCoinListesi by mutableStateOf(listOf<CoinYaniti>())

    // Favori listesini uygulama açıldığında hafızadan (SharedPreferences) yüklüyoruz
    var favoriListesi by mutableStateOf(hafiza.favorileriOku())

    val currentPrice: Double get() = fiyatlar["BTCUSDT"] ?: 0.0
    val previousPrice: Double get() = eskiFiyatlar["BTCUSDT"] ?: 0.0

    // Bütün popüler coinleri ilk açılışta çeker
    fun herSeyiGetir() {
        viewModelScope.launch {
            try {
                val yanit = api.getButunCoinleri()
                tumCoinListesi = yanit
                yanit.forEach { coin ->
                    fiyatGuncelle(coin.symbol, coin.lastPrice.toDouble(), coin.priceChangePercent)
                }
            } catch (e: Exception) {
                // Hata yönetimi buraya eklenebilir
            }
        }
    }

    // Favori ekleme/çıkarma ve hafızaya kaydetme mantığı
    fun favoriDurumunuDegistir(sembol: String) {
        val suAnkiFavoriler = favoriListesi.toMutableSet()
        
        if (suAnkiFavoriler.contains(sembol)) {
            suAnkiFavoriler.remove(sembol)
        } else {
            suAnkiFavoriler.add(sembol)
        }
        
        // State'i güncelle (UI'ın değişmesi için)
        favoriListesi = suAnkiFavoriler
        
        // Hafızaya kalıcı olarak kaydet
        hafiza.favorileriKaydet(suAnkiFavoriler)
    }

    // Soketten gelen canlı verileri işleme
    fun fiyatGuncelle(sembol: String, yeniFiyat: Double, yeniYuzde: String) {
        val suAnkiFiyat = fiyatlar[sembol] ?: 0.0
        
        // Fiyat değiştiyse eski fiyatı kaydet (Renk değişimi için)
        if (yeniFiyat != suAnkiFiyat) {
            eskiFiyatlar = eskiFiyatlar + (sembol to suAnkiFiyat)
            fiyatlar = fiyatlar + (sembol to yeniFiyat)
        }
        yuzdeler = yuzdeler + (sembol to yeniYuzde)
    }

    fun favoriDegistir(symbol: String) {}
}
