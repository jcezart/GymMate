package com.example.gymmate.domain.repository

import com.example.gymmate.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun addCategory(category: Category)
    suspend fun deleteCategory(name: String)
}


//Beneficio de separar entre repository e repository implementation
//Repository = regra de negócio, interface (classe abstrata)
//Impl = implementação da regra de negócio

//##Exemplo 1:
//possibilidade de utilizar o Repository em mais de um tipo de implementação
//como por exemplo: no BD como está agora e também no Firebase

//##Exemplo 2:
//não ferir o single responsability (SOLID), ter a classe com uma responsabilidade só

//##Exemplo 3:
//arquitetura limpa, separação por camadas, DOMAIN = camada de negócios, DATA não deve ter
//contato com o presentation, DATA consome DOMAIN e PRESENTATION consome DOMAIN também

//##Exemplo 4:
//Repository é utilizado pelo VIEWMODEL, através da DI