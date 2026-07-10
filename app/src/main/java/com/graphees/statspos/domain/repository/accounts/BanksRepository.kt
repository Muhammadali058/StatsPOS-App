package com.graphees.statspos.domain.repository.accounts

import com.graphees.statspos.domain.models.accounts.Accounts
import com.graphees.statspos.domain.models.accounts.Banks
import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface BanksRepository{
    // region Banks
    suspend fun loadBanks(body: JsonObject): Resource<JsonObject>

    suspend fun insertBank(bank: Banks): Resource<JsonObject>

    suspend fun updateBank(bank: Banks): Resource<JsonObject>

    suspend fun deleteBank(id: Long): Resource<JsonObject>

    suspend fun getBank(id: Long): Resource<JsonObject>
    // endregion

    // region Sub-Banks
    suspend fun loadSubBanks(body: JsonObject): Resource<JsonObject>

    suspend fun insertSubBank(subBank: Accounts): Resource<JsonObject>

    suspend fun updateSubBank(subBank: Accounts): Resource<JsonObject>

    suspend fun deleteSubBank(id: Long): Resource<JsonObject>

    suspend fun getSubBank(id: Long): Resource<JsonObject>
    // endregion
}