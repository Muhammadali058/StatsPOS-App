package com.example.statspos.data.repository.utilities

import com.example.statspos.data.remote.utilities.UsersApi
import com.example.statspos.domain.models.utilities.users.UserRights
import com.example.statspos.domain.models.utilities.users.Users
import com.example.statspos.domain.repository.main.ClientsRepository
import com.example.statspos.domain.repository.utilities.UsersRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class UsersRepositoryImpl @Inject constructor(
    private val api: UsersApi
) : UsersRepository {
    override suspend fun login(username: String, password: String): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("username", username)
            addProperty("password", password)
        }

        return safeApiCall {
            api.login(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadUsers(body: JsonObject): Resource<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun insertUser(
        user: Users,
        userRights: UserRights
    ): Resource<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(
        id: Long,
        user: Users,
        userRights: UserRights
    ): Resource<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(id: Long): Resource<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun getUser(id: Long): Resource<JsonObject> {
        TODO("Not yet implemented")
    }
}
