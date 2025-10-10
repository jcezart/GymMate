package com.example.gymmate.domain.repository

import com.example.gymmate.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>

    suspend fun addCategory(category: Category)
    suspend fun addCategoryWithPlus(): Boolean
    suspend fun deleteCategory(name: String)
}


// Data
// data source

//Local
//database -GymMateDataBase
//enntity-CategoryEntity
//dao-ExerciseDAO -> Funcoes do database - Room


//mapper - CategoryMapper -> CategoryEntity.toDomain()


//RepositoryIplm :Repository
//funcoes do dao -> retornar um flow com data class Category



//Domain
//
//Defino Models
//Category
//Exercise


//Repository
// As funcoes Utilizadas
//getAllCategories

