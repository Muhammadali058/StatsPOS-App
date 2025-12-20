package com.example.statspos.data.repository.utilities

import com.example.statspos.data.remote.utilities.UsersApi
import com.example.statspos.domain.repository.main.ClientsRepository
import com.example.statspos.domain.repository.utilities.UsersRepository
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val api: UsersApi
) : UsersRepository {
    override suspend fun login(body: JsonObject): Resource<JsonObject>  = safeApiCall { api.login(body) }
}
