package com.graphees.statspos.domain.repository.main

import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface ClientsRepository {
    suspend fun clientLogin(body: JsonObject): Resource<JsonObject>
    suspend fun clientSignup(body: JsonObject): Resource<JsonObject>
    suspend fun getBranches(clientId: Int): Resource<JsonObject>
    suspend fun getClient(clientId: Int): Resource<JsonObject>
    suspend fun updateShoppingAppFCMToken(fcmToken:String): Resource<JsonObject>
}