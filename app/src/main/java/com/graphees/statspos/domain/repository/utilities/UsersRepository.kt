package com.graphees.statspos.domain.repository.utilities

import com.graphees.statspos.domain.models.utilities.users.UserRights
import com.graphees.statspos.domain.models.utilities.users.Users
import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface UsersRepository {
    suspend fun login(username: String, password: String): Resource<JsonObject>

    suspend fun loadUsers(body: JsonObject): Resource<JsonObject>
    
    suspend fun insertUser(user: Users, userRights: UserRights): Resource<JsonObject>

    suspend fun updateUser(id:Long, user: Users, userRights: UserRights): Resource<JsonObject>

    suspend fun deleteUser(id: Long): Resource<JsonObject>

    suspend fun getUser(id: Long): Resource<JsonObject>
}