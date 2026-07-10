package com.graphees.statspos.data.repository.reports

import com.graphees.statspos.data.remote.reports.ItemsReportsApi
import com.graphees.statspos.domain.repository.reports.ItemsReportsRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class ItemsReportsRepositoryImpl @Inject constructor(
    private val api: ItemsReportsApi
) : ItemsReportsRepository {
    override suspend fun itemsList(body: JsonObject): Resource<JsonObject> {
        body.addProperty("branchGroupId", HP.branchGroupId)
        body.addProperty("host", "${DB.HOST}/${HP.clientId}/images/")

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
