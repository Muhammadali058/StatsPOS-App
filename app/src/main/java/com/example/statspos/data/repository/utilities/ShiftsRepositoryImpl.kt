package com.example.statspos.data.repository.utilities

import com.example.statspos.data.remote.utilities.ShiftsApi
import com.example.statspos.domain.repository.utilities.ShiftsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class ShiftsRepositoryImpl @Inject constructor(
    private val api: ShiftsApi
) : ShiftsRepository {
    override suspend fun openShift(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.openShift(
                DB.addParams(body)
            )
        }
    }

    override suspend fun closeShift(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.closeShift(
                DB.addParams(body)
            )
        }
    }

    override suspend fun passEntry(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.passEntry(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadUserShifts(userId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("userId", userId)
        }
        return safeApiCall {
            api.loadUserShifts(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getShiftDetails(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.getShiftDetails(
                DB.addParams(body)
            )
        }
    }
}
