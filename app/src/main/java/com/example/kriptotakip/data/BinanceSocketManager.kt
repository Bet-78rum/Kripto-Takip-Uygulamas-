package com.betul.kriptotakip.data

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import org.json.JSONArray

class BinanceSocketManager(private val onDataReceived: (String, Double, String) -> Unit) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun start() {
        // Tüm marketteki verileri canlı almak için !ticker@arr kanalını kullanıyoruz
        val request = Request.Builder()
            .url("wss://stream.binance.com:9443/ws/!ticker@arr")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    // !ticker@arr bir liste döner
                    val jsonArray = JSONArray(text)
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val symbol = json.getString("s")
                        val price = json.getString("c").toDouble()
                        val percent = json.getString("P")

                        onDataReceived(symbol, price, percent)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun stop() {
        webSocket?.close(1000, "App closed")
        webSocket = null
    }
}
