package com.example.gymmate.di

import com.example.gymmate.data.datasource.local.dao.ExerciseDAO
import com.example.gymmate.data.datasource.local.db.GymMateDataBase
import com.example.gymmate.data.repository.CategoryRepositoryImpl
import com.example.gymmate.data.repository.ExerciseRepositoryImpl
import com.example.gymmate.domain.repository.CategoryRepository
import com.example.gymmate.domain.repository.ExerciseRepository
import com.example.gymmate.presentation.viewmodel.GymMateViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

//    GymMateViewModel depende de Repository.
//    RepositoryImpl depende de DAO.
//    DAO depende de Database.
//    Database depende de Context.
//    Koin cria/conecta tudo.

val appModule = module {
    // como cria o GymMateDataBase?
     // chamando getDatabase(context)
    single {
        GymMateDataBase.getDatabase(androidContext())
    }

    //como criar o ExerciseDAO?
     //pega o banco e chama o exerciseDao()
    single<ExerciseDAO> {
        get<GymMateDataBase>().exerciseDao()
    }

    //como criar o ExerciseRepository?
     //cria a implementacao, passando o exerciseDAO
    single<ExerciseRepository> {
        ExerciseRepositoryImpl(dao = get())
    }

    single<CategoryRepository> {
        CategoryRepositoryImpl(dao = get())
    }

    //como criar o GymMateViewModel?
     //passando category e exerciseRepository
    viewModel {
        GymMateViewModel(
            categoryRepository = get(),
            exerciseRepository = get()
        )
    }


}