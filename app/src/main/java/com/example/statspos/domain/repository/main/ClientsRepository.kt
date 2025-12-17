package com.example.statspos.domain.repository.main

import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface ClientsRepository {
    suspend fun clientLogin(body: JsonObject): Resource<JsonObject>
}