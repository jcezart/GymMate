package com.example.gymmate.data.repository

import com.example.gymmate.data.local.dao.ExerciseDAO
import com.example.gymmate.data.mapper.toDomain
import com.example.gymmate.data.mapper.toEntity
import com.example.gymmate.domain.model.Category
import com.example.gymmate.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val dao: ExerciseDAO
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> =
        dao.getAllCategories().map { list -> list.map { it.toDomain() } }

    override suspend fun addCategory(category: Category) {
        dao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory(name: String) {
        dao.deleteCategory(name)
    }
}
