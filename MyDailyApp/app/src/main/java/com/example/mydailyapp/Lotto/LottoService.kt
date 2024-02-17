package com.example.mydailyapp.Lotto

import com.example.mydailyapp.models.LottoResponse
import retrofit2.http.GET

interface LottoService {
    @GET("common.do?method=getLottoNumber")
    suspend fun getLatestLottoNumbers(): LottoResponse
}

