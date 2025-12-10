package com.example.gymmate.data.datasource.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gymmate.data.datasource.local.dao.ExerciseDAO
import com.example.gymmate.data.datasource.local.entity.CategoryEntity
import com.example.gymmate.data.datasource.local.entity.ExerciseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ExerciseEntity::class, CategoryEntity::class], version = 7, exportSchema = false)
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
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Insere categorias iniciais ao criar o banco
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = getDatabase(context).exerciseDao()
                                dao.insertCategory(CategoryEntity("Workout A"))
                                dao.insertCategory(CategoryEntity("Workout B"))
                                dao.insertCategory(CategoryEntity("Workout C"))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE exercises ADD COLUMN category TEXT NOT NULL DEFAULT 'Workout A'"
                )
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `categories` (`name` TEXT NOT NULL, PRIMARY KEY(`name`))"
                )
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {

                // 1. Criar nova tabela
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS categories_new (
                nameDb TEXT NOT NULL,
                PRIMARY KEY(nameDb)
            )
        """)

                // 2. Copiar os dados da tabela antiga
                db.execSQL("""
            INSERT INTO categories_new (nameDb)
            SELECT name FROM categories
        """)

                // 3. Remover tabela antiga
                db.execSQL("DROP TABLE categories")

                // 4. Renomear nova tabela
                db.execSQL("ALTER TABLE categories_new RENAME TO categories")
            }
        }

    }
}