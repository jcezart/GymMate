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
import com.example.gymmate.data.datasource.local.entity.WorkoutSessionEntity
import com.example.gymmate.data.datasource.local.entity.WorkoutSessionExerciseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.gymmate.data.datasource.local.dao.WorkoutSessionDao
import com.example.gymmate.data.datasource.local.dao.WorkoutSessionExerciseDao

@Database(entities = [
            ExerciseEntity::class,
            CategoryEntity::class,
            WorkoutSessionEntity::class,
            WorkoutSessionExerciseEntity::class], version = 9, exportSchema = false)
abstract class GymMateDataBase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDAO
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun workoutSessionExerciseDao(): WorkoutSessionExerciseDao

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
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9)
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
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE exercises ADD COLUMN position INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    """
                    UPDATE exercises
                    SET position = (
                        SELECT COUNT(*) - 1
                        FROM exercises AS other
                        WHERE other.category = exercises.category
                            AND (
                                other.exerciseName < exercises.exerciseName
                                OR (
                                    other.exerciseName = exercises.exerciseName
                                    AND other.rowid <= exercises.rowid
                                )
                            )
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS workout_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                category TEXT NOT NULL,
                startedAt INTEGER NOT NULL,
                finishedAt INTEGER,
                status TEXT NOT NULL
            )
            """.trimIndent()
                )

                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS workout_session_exercises (
                sessionId INTEGER NOT NULL,
                exerciseId TEXT NOT NULL,
                exerciseName TEXT NOT NULL,
                sets INTEGER NOT NULL,
                reps INTEGER NOT NULL,
                weight REAL NOT NULL,
                position INTEGER NOT NULL,
                completedSets INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(sessionId, exerciseId)
            )
            """.trimIndent()
                )
            }
        }

    }
}