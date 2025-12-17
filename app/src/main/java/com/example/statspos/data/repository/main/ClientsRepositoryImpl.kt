package com.example.statspos.data.repository.main

import com.example.statspos.data.remote.main.ClientsApi
import com.example.statspos.domain.repository.main.ClientsRepository
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import javax.inject.Inject

class ClientsRepositoryImpl @Inject constructor(
    private val api: ClientsApi
) : ClientsRepository {
    override suspend fun clientLogin(body: JsonObject): Resource<JsonObject> {
        val result = safeApiCall { api.clientLogin(body) }

        return when (result) {
            is Resource.Error -> Resource.Error(result.message)
            is Resource.Information -> Resource.Information(result.infoMessage)
            is Resource.Success -> Resource.Success(result.data)
        }
    }

}
