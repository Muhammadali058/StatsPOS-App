package com.example.statspos.domain.repository.utilities

import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface ShiftsRepository {
    suspend fun openShift(userId:Long): Resource<JsonObject>

    suspend fun closeShift(userId:Long, cashInHand: Long): Resource<JsonObject>

    suspend fun passEntry(body: JsonObject): Resource<JsonObject>

    suspend fun loadUserShifts(userId: Long): Resource<JsonObject>

    suspend fun getShiftDetails(body: JsonObject): Resource<JsonObject>
}