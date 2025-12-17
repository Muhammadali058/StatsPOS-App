package com.example.statspos.di

import com.example.statspos.data.repository.items.CategoriesRepositoryImpl
import com.example.statspos.data.repository.main.ClientsRepositoryImpl
import com.example.statspos.domain.repository.items.CategoriesRepository
import com.example.statspos.domain.repository.main.ClientsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ApiModule {

    @Binds
    abstract fun bindsCategoriesRepo(categoriesRepoImpl: CategoriesRepositoryImpl): CategoriesRepository

    @Binds
    abstract fun bindsClientsRepo(clientsRepoImpl: ClientsRepositoryImpl): ClientsRepository

}