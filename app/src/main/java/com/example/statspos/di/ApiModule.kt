package com.example.statspos.di

import com.example.statspos.data.repository.CategoriesRepositoryImpl
import com.example.statspos.domain.repository.CategoriesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ApiModule {

    @Binds
    abstract fun bindsCategoriesRepo(categoriesRepoImpl: CategoriesRepositoryImpl): CategoriesRepository

}