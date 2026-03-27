package com.betul.kriptotakip.data.remote

import com.betul.kriptotakip.domain.CoinResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceApi {

    @GET("api/v3/ticker/24hr")
    suspend fun getCoinDetails(
        @Query("symbol") symbol: String
    ): CoinResponse

    @GET("api/v3/ticker/24hr")
    suspend fun getAllCoins(): List<CoinResponse>
}
