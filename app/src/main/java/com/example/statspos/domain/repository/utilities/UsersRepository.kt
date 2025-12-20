package com.example.statspos.domain.repository.utilities

import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface UsersRepository {
    suspend fun login(body: JsonObject): Resource<JsonObject>
}