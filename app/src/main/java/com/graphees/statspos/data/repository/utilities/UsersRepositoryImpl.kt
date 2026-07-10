package com.graphees.statspos.data.repository.utilities

import com.graphees.statspos.data.remote.utilities.UsersApi
import com.graphees.statspos.domain.models.utilities.users.UserRights
import com.graphees.statspos.domain.models.utilities.users.Users
import com.graphees.statspos.domain.repository.utilities.UsersRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
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
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.loadUsers(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertUser(user: Users, userRights: UserRights): Resource<JsonObject> {
        val body = JsonObject().apply {
            add("user", DB.getJsonObject(user))
            add("userRights", DB.getJsonObject(userRights))
            addProperty("branchGroupId", HP.branchGroupId)
        }
        return safeApiCall {
            api.insertUser(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateUser(id: Long, user: Users, userRights: UserRights): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            add("user", DB.getJsonObject(user))
            add("userRights", DB.getJsonObject(userRights))
            addProperty("branchGroupId", HP.branchGroupId)
        }
        return safeApiCall {
            api.updateUser(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteUser(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
            addProperty("branchGroupId", HP.branchGroupId)
        }
        return safeApiCall {
            api.deleteUser(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getUser(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getUser(
                DB.addParams(body)
            )
        }
    }
}
