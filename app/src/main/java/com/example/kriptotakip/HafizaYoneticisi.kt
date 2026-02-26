

package com.example.kriptotakip.data // Paket isminin doğruluğunu kontrol et

import android.content.Context
import android.content.SharedPreferences

class HafizaYoneticisi(context: Context) {

    // "ayarlar" adında bir hafıza alanı oluşturuyoruz
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("kripto_ayarlar", Context.MODE_PRIVATE)

    // Modu kaydetme fonksiyonu
    fun temaKaydet(isKaranlik: Boolean) {
        sharedPreferences.edit().putBoolean("karanlik_mod", isKaranlik).apply()
    }

    // Kayıtlı modu okuma fonksiyonu (Kayıt yoksa false döner)
    fun temaOku(): Boolean {
        return sharedPreferences.getBoolean("karanlik_mod", false)
    }
    // Favori listesini kaydet (Örn: ["BTCUSDT", "ETHUSDT"])
    fun favorileriKaydet(favoriler: Set<String>) {
        sharedPreferences.edit().putStringSet("favori_coinler", favoriler).apply()
    }

    // Favori listesini oku
    fun favorileriOku(): Set<String> {
        return sharedPreferences.getStringSet("favori_coinler", emptySet()) ?: emptySet()
    }
}