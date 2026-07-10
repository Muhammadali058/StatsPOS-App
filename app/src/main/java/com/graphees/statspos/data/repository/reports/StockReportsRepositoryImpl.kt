package com.graphees.statspos.data.repository.reports

import com.graphees.statspos.data.remote.reports.StockReportsApi
import com.graphees.statspos.domain.repository.reports.StockReportsRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class StockReportsRepositoryImpl @Inject constructor(
    private val api: StockReportsApi
) : StockReportsRepository {
    override suspend fun mainReport(body: JsonObject): Resource<JsonObject> {
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.mainReport(
                DB.addParams(body)
            )
        }
    }

    override suspend fun stockReport(body: JsonObject): Resource<JsonObject> {
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.stockReport(
                DB.addParams(body)
            )
        }
    }
}
