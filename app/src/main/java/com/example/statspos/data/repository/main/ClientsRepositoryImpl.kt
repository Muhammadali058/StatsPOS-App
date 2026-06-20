package com.example.statspos.data.repository.main

import com.example.statspos.data.remote.main.ClientsApi
import com.example.statspos.domain.repository.main.ClientsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import javax.inject.Inject

class ClientsRepositoryImpl @Inject constructor(
    private val api: ClientsApi
) : ClientsRepository {
    override suspend fun clientLogin(body: JsonObject): Resource<JsonObject> =
        safeApiCall { api.clientLogin(body) }

    override suspend fun clientSignup(body: JsonObject): Resource<JsonObject>  = safeApiCall { api.clientSignup(body) }

    override suspend fun getBranches(clientId: Int): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("clientId", clientId)
        }

        return safeApiCall {
            api.getBranches(
                body
            )
        }
    }

    override suspend fun getClient(clientId: Int): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", clientId)
        }

        return safeApiCall {
            api.getClient(
                body
            )
        }
    }

}
