package com.example.kriptotakip.data.remote

import com.example.kriptotakip.domain.CoinYaniti
import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceApi {

    // 1. Tek bir coinin detaylarını (fiyat + yüzde) almak için:
    @GET("api/v3/ticker/24hr")
    suspend fun getCoinDetay(
        @Query("symbol") sembol: String
    ): CoinYaniti

    // 2. Senin bulduğun o dev listeyi (tüm marketi) çekmek için:
    @GET("api/v3/ticker/24hr")
    suspend fun getButunCoinleri(): List<CoinYaniti>
}
