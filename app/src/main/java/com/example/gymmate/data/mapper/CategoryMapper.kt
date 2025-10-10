package com.example.gymmate.data.mapper

import com.example.gymmate.data.datasource.local.entity.CategoryEntity
import com.example.gymmate.domain.model.Category

fun CategoryEntity.toDomain(): Category =
    Category(name = nameDb)



fun Category.toEntity(): CategoryEntity =
    CategoryEntity(nameDb = name)



// <- Domain-> Presentation

// Domain nao pode chamar outras camadas
//Presentation so chama o domain e nao pode chamar o data
//Data so chama o domain e nao pode chamar o presentation



//ViewModel ->Screen -> State->Action || Event


