package com.example.kriptotakip.data

import android.content.Context
import android.content.SharedPreferences

class StorageManager(context: Context) {

    // "crypto_settings" adında bir hafıza alanı oluşturuyoruz
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("crypto_settings", Context.MODE_PRIVATE)

    // Modu kaydetme fonksiyonu
    fun saveTheme(isDark: Boolean) {
        sharedPreferences.edit().putBoolean("dark_mode", isDark).apply()
    }

    // Kayıtlı modu okuma fonksiyonu (Kayıt yoksa false döner)
    fun getTheme(): Boolean {
        return sharedPreferences.getBoolean("dark_mode", false)
    }

    // Favori listesini kaydet (Örn: ["BTCUSDT", "ETHUSDT"])
    fun saveFavorites(favorites: Set<String>) {
        sharedPreferences.edit().putStringSet("favorite_coins", favorites).apply()
    }

    // Favori listesini oku
    fun getFavorites(): Set<String> {
        return sharedPreferences.getStringSet("favorite_coins", emptySet()) ?: emptySet()
    }
}
