package com.example.mydailyapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mydailyapp.converters.Converters
import com.example.mydailyapp.dao.TaskDao
import com.example.mydailyapp.models.Task

@Database(
    entities = [Task::class],
    version = 2,
    exportSchema = true
)

@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {

    abstract val taskDao : TaskDao

    companion object{
        @Volatile
        private var INSTANCE : TaskDatabase? = null

        fun getInstance(context: Context) :  TaskDatabase {
            synchronized(this){
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    "task_db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also{
                    INSTANCE = it
                }
            }
        }

    }

}