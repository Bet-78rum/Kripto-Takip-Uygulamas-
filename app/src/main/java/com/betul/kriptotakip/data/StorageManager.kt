package com.betul.kriptotakip.data

import android.content.Context
import android.content.SharedPreferences

class StorageManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("crypto_settings", Context.MODE_PRIVATE)

    // Modu kaydetme
    fun saveTheme(isDark: Boolean) {
        sharedPreferences.edit().putBoolean("dark_mode", isDark).apply()
    }

    // Kayıtlı modu okuma
    fun getTheme(): Boolean {
        return sharedPreferences.getBoolean("dark_mode", false)
    }

    // Favori listesini kaydet
    fun saveFavorites(favorites: Set<String>) {
        sharedPreferences.edit().putStringSet("favorite_coins", favorites).apply()
    }

    // Favori listesini oku
    fun getFavorites(): Set<String> {
        return sharedPreferences.getStringSet("favorite_coins", emptySet()) ?: emptySet()
    }

    // Favorileri tamamen temizle (Çıkış yaparken kullanılır)
    fun clearFavorites() {
        sharedPreferences.edit().remove("favorite_coins").apply()
    }
}
