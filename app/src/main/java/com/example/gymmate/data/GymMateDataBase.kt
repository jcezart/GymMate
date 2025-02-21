package com.example.gymmate.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Exercise::class, Category::class], version = 6, exportSchema = false)
abstract class GymMateDataBase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDAO

    companion object {
        @Volatile
        private var INSTANCE: GymMateDataBase? = null

        fun getDatabase(context: Context): GymMateDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymMateDataBase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Insere categorias iniciais ao criar o banco
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).exerciseDao()
                                dao.insertCategory(Category("Workout A"))
                                dao.insertCategory(Category("Workout B"))
                                dao.insertCategory(Category("Workout C"))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE exercises ADD COLUMN category TEXT NOT NULL DEFAULT 'Workout A'"
                )
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `categories` (`name` TEXT NOT NULL, PRIMARY KEY(`name`))"
                )
                // Não precisa inserir aqui, pois o callback onCreate cuida disso
            }
        }
    }
}