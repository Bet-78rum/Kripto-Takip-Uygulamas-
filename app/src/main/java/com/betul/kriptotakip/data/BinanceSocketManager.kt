package com.betul.kriptotakip.data

import android.util.Log
import okhttp3.*
import org.json.JSONArray

class BinanceSocketManager(private val onDataReceived: (String, Double, String) -> Unit) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun start() {
        val request = Request.Builder()
            .url("wss://stream.binance.com:9443/ws/!ticker@arr")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val jsonArray = JSONArray(text)
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val symbol = json.getString("s")
                        val price = json.getString("c").toDouble()
                        val percent = json.getString("P")
                        Log.e("BinanceSocket", "data received: $symbol, $price, $percent")
                        onDataReceived(symbol, price, percent)
                    }
                } catch (e: Exception) {
                    Log.e("BinanceSocket", "Parse Hatası: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("BinanceSocket", "Bağlantı koptu, 8 saniye sonra tekrar denenecek...")
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ start() }, 8000)
            }
        })
    }

    fun stop() {
        webSocket?.close(1000, "App closed")
    }
}
