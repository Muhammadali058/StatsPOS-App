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

    //    {
//        return safeApiCall { api.clientLogin(body) }
////        val result = safeApiCall { api.clientLogin(body) }
////
////        return when (result) {
////            is Resource.Error -> Resource.Error(result.message)
////            is Resource.Information -> Resource.Information(result.infoMessage)
////            is Resource.Success -> Resource.Success(result.data)
////        }
//    }

    override suspend fun clientSignup(body: JsonObject): Resource<JsonObject>  = safeApiCall { api.clientSignup(body) }

    override suspend fun localClientLogin(body: JsonObject): Resource<JsonObject> =
        safeApiCall { api.localClientLogin(body) }


    override suspend fun getBranches(): Resource<JsonObject> {
        return safeApiCall {
            api.getBranches(
                DB.addParams(JsonObject())
            )
        }
    }

}
