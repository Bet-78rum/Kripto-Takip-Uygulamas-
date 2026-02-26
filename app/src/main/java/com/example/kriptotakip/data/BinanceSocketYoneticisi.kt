package com.example.kriptotakip.data

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class BinanceSocketYoneticisi(private val onVeriGeldi: (String, Double, String) -> Unit) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun baslat() {
        // Tüm marketteki verileri canlı almak için !ticker@arr kanalını kullanıyoruz
        val request = Request.Builder()
            .url("wss://stream.binance.com:9443/ws/!ticker@arr")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    // !ticker@arr bir liste döner
                    val jsonArray = org.json.JSONArray(text)
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val sembol = json.getString("s")
                        val fiyat = json.getString("c").toDouble()
                        val yuzde = json.getString("P")

                        onVeriGeldi(sembol, fiyat, yuzde)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    fun durdur() {
        webSocket?.close(1000, "Uygulama kapandı")
        webSocket = null
    }
}
