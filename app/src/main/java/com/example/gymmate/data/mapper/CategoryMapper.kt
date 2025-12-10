package com.example.gymmate.data.mapper

import com.example.gymmate.data.datasource.local.entity.CategoryEntity
import com.example.gymmate.domain.model.Category

fun CategoryEntity.toDomain(): Category =
    Category(name = nameDb)
fun Category.toEntity(): CategoryEntity =
    CategoryEntity(nameDb = name)
