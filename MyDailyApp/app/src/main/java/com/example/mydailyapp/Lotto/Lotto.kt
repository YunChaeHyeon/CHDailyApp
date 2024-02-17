package com.example.mydailyapp.models

import android.widget.TextView
import com.example.mydailyapp.Lotto.LottoService
import com.example.mydailyapp.R
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class LottoResponse(
    val drwNo: Int,
    val drwtNo1: Int,
    val drwtNo2: Int,
    val drwtNo3: Int,
    val drwtNo4: Int,
    val drwtNo5: Int,
    val drwtNo6: Int
)

suspend fun fetchLatestLottoNumbers(): LottoResponse {
    // Retrofit 객체 생성
    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.dhlottery.co.kr/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Retrofit을 사용하여 API 호출을 위한 서비스 인스턴스 생성
    val service = retrofit.create(LottoService::class.java)

    // API 호출 및 응답 반환
    return service.getLatestLottoNumbers()
}