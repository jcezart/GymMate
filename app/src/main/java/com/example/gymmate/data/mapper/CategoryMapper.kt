package com.example.gymmate.data.mapper

import com.example.gymmate.data.local.entity.CategoryEntity
import com.example.gymmate.domain.model.Category

fun CategoryEntity.toDomain(): Category =
    Category(name = name)

fun Category.toEntity(): CategoryEntity =
    CategoryEntity(name = name)
