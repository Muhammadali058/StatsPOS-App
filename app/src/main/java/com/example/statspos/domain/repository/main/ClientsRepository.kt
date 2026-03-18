package com.example.statspos.domain.repository.main

import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface ClientsRepository {
    suspend fun clientLogin(body: JsonObject): Resource<JsonObject>
    suspend fun clientSignup(body: JsonObject): Resource<JsonObject>
    suspend fun localClientLogin(body: JsonObject): Resource<JsonObject>

    suspend fun getBranches(): Resource<JsonObject>
}