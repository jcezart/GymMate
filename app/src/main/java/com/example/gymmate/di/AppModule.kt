package com.example.gymmate.di

import androidx.room.Room
import com.example.gymmate.data.datasource.local.dao.ExerciseDAO
import com.example.gymmate.data.datasource.local.db.GymMateDataBase
import com.example.gymmate.data.repository.CategoryRepositoryImpl
import com.example.gymmate.data.repository.ExerciseRepositoryImpl
import com.example.gymmate.domain.repository.CategoryRepository
import com.example.gymmate.domain.repository.ExerciseRepository
import com.example.gymmate.presentation.viewmodel.GymMateViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single {
        GymMateDataBase.getDatabase(androidContext())
    }

    // DAO
    single<ExerciseDAO> {
        get<GymMateDataBase>().exerciseDao()
    }

    // Repositories
    single<ExerciseRepository> {
        ExerciseRepositoryImpl(dao = get())
    }

    single<CategoryRepository> {
        CategoryRepositoryImpl(dao = get())
    }

    // ViewModels
    viewModel {
        GymMateViewModel(
            categoryRepository = get(),
            exerciseRepository = get()
        )
    }
}
