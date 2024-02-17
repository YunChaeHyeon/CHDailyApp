package com.example.mydailyapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mydailyapp.models.Lotto
import com.example.mydailyapp.models.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface LottoDao {

    @Query("SELECT * FROM Lotto ORDER BY date DESC")
    fun getLottoList() : Flow<List<Lotto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLotto(lotto: Lotto): Long

    @Query("DELETE FROM Lotto WHERE id == :id")
    suspend fun deleteLottoUsingId(id: String) : Int

}