package com.example.mydailyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mydailyapp.converters.Converters
import com.example.mydailyapp.dao.LottoDao
import com.example.mydailyapp.dao.TaskDao
import com.example.mydailyapp.models.Lotto

@Database(
    entities = [Lotto::class],
    version = 2,
    exportSchema = true
)

@TypeConverters(Converters::class)
abstract class LottoDatabase : RoomDatabase() {

    abstract val lottoDao : LottoDao

    companion object{
        @Volatile
        private var INSTANCE : LottoDatabase? = null

        fun getInstance(context: Context) :  LottoDatabase {
            synchronized(this){
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    LottoDatabase::class.java,
                    "lotto_db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also{
                        INSTANCE = it
                    }
            }
        }

    }

}