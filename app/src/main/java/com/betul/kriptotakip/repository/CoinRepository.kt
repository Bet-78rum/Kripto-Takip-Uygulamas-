package com.betul.kriptotakip.repository

import com.betul.kriptotakip.data.BinanceSocketManager
import com.betul.kriptotakip.data.StorageManager
import com.betul.kriptotakip.data.remote.RetrofitClient
import com.betul.kriptotakip.domain.CoinResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CoinRepository(private val storage: StorageManager) {
    private val api = RetrofitClient.api
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun getAllCoins(): List<CoinResponse> {
        return api.getAllCoins()
    }

    suspend fun getUserData(): Triple<Set<String>, Boolean, Long> {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            try {
                val document = firestore.collection("users").document(userId).get().await()
                val favorites = (document.get("favorites") as? List<String>)?.toSet() ?: emptySet()
                val isDarkMode = document.getBoolean("isDarkMode") ?: false
                val profileColor = document.getLong("profileColor") ?: 0xFF1ABC9CL
                return Triple(favorites, isDarkMode, profileColor)
            } catch (e: Exception) { }
        }
        return Triple(storage.getFavorites(), storage.getTheme(), 0xFF1ABC9CL)
    }

    fun saveUserData(favorites: Set<String>, isDarkMode: Boolean, profileColor: Long) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val data = mapOf(
                "favorites" to favorites.toList(),
                "isDarkMode" to isDarkMode,
                "profileColor" to profileColor
            )
            firestore.collection("users").document(userId).update(data)
        }
    }

    fun updateUserName(newName: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId).update("name", newName)
        }
    }

    // Geri çağırma tipini (symbol, price, percent) eskisi gibi tekil yaptık
    fun startSocket(onDataReceived: (String, Double, String) -> Unit): BinanceSocketManager {
        val socket = BinanceSocketManager(onDataReceived)
        socket.start()
        return socket
    }
}
