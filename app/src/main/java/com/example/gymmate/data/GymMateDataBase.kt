package com.example.gymmate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Exercise::class], version = 4, exportSchema = false)
abstract class GymMateDataBase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDAO

    companion object {
        @Volatile
        private var INSTANCE: GymMateDataBase? = null

        fun getDatabase(context: Context): GymMateDataBase {
            // Retorna a instância existente ou cria uma nova instância do banco de dados
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymMateDataBase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}