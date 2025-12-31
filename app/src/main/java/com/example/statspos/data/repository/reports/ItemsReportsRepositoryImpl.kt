package com.example.statspos.data.repository.reports

import com.example.statspos.data.remote.reports.ItemsReportsApi
import com.example.statspos.domain.repository.reports.ItemsReportsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class ItemsReportsRepositoryImpl @Inject constructor(
    private val api: ItemsReportsApi
) : ItemsReportsRepository {
    override suspend fun itemsList(body: JsonObject): Resource<JsonObject> {
        body.addProperty("branchGroupId", HP.branchGroupId)
        body.addProperty("host", DB.HOST)

        return safeApiCall {
            api.itemsList(
                DB.addParams(body)
            )
        }
    }

    override suspend fun itemsRateChangedList(body: JsonObject): Resource<JsonObject> {
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.itemsRateChangedList(
                DB.addParams(body)
            )
        }
    }
}
