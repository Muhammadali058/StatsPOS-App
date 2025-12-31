package com.example.statspos.data.repository.reports

import com.example.statspos.data.remote.reports.StockReportsApi
import com.example.statspos.domain.repository.reports.StockReportsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class StockReportsRepositoryImpl @Inject constructor(
    private val api: StockReportsApi
) : StockReportsRepository {
    override suspend fun stockReport(body: JsonObject): Resource<JsonObject> {
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.stockReport(
                DB.addParams(body)
            )
        }
    }
}
