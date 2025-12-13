package com.example.statspos.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.statspos.data.remote.CategoriesApi
import com.example.statspos.utils.LocalDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class NetworkModule {

    @Provides
    fun provideLocalDataStore(dataStore: DataStore<Preferences>): LocalDataStore{
        return LocalDataStore(dataStore)
    }

    @Provides
    fun provideCategoriesApi(retrofitInstance: Retrofit): CategoriesApi {
        return retrofitInstance.create(CategoriesApi::class.java)
    }

}