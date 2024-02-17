package com.example.mydailyapp.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Lotto (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String,
    val lottoNum1 : Int,
    val lottoNum2 : Int,
    val lottoNum3 : Int,
    val lottoNum4 : Int,
    val lottoNum5 : Int,
    val lottoNum6 : Int,
    val date : Date
)