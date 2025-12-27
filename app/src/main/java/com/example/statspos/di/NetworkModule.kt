package com.example.statspos.di

import com.example.statspos.data.remote.items.CategoriesApi
import com.example.statspos.data.remote.main.ClientsApi
import com.example.statspos.data.remote.utilities.UsersApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
class NetworkModule {

//    @Provides
//    fun provideLocalDataStore(dataStore: DataStore<Preferences>): LocalDataStore{
//        return LocalDataStore(dataStore)
//    }

    @Provides
    fun provideCategoriesApi(retrofitInstance: Retrofit): CategoriesApi {
        return retrofitInstance.create(CategoriesApi::class.java)
    }

    @Provides
    fun provideUsersApi(retrofitInstance: Retrofit): UsersApi {
        return retrofitInstance.create(UsersApi::class.java)
    }

    @Provides
    fun provideClientsApi(retrofitInstance: Retrofit): ClientsApi {
        return retrofitInstance.create(ClientsApi::class.java)
    }

}