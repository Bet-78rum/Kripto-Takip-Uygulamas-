package com.betul.kriptotakip.data

import android.util.Log
import com.google.gson.Gson
import okhttp3.*

class BinanceSocketManager(private val onDataReceived: (String, Double, String) -> Unit) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val gson = Gson()

    fun start() {
        val request = Request.Builder()
            .url("wss://stream.binance.com:9443/stream?streams=btcusdt@ticker/ethusdt@ticker/bnbusdt@ticker/solusdt@ticker/xrpusdt@ticker/adausdt@ticker/dogeusdt@ticker/trxusdt@ticker/dotusdt@ticker/ltcusdt@ticker/avaxusdt@ticker/shibusdt@ticker/linkusdt@ticker/maticusdt@ticker/uniusdt@ticker")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val response = gson.fromJson(text, BinanceResponse::class.java)
                    val ticker = response.data
                    val symbol = ticker.symbol
                    val price = ticker.lastPrice.toDouble()
                    val percent = ticker.priceChangePercent
                    
                    Log.d("BinanceSocket", "data received: $symbol, $price, $percent")
                    onDataReceived(symbol, price, percent)
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
