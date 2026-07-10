package com.graphees.statspos.data.repository.reports

import com.graphees.statspos.data.remote.reports.PurchaseReportsApi
import com.graphees.statspos.domain.repository.reports.PurchaseReportsRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import javax.inject.Inject

class PurchaseReportsRepositoryImpl @Inject constructor(
    private val api: PurchaseReportsApi
) : PurchaseReportsRepository {
    override suspend fun mainReport(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.mainReport(
                DB.addParams(body)
            )
        }
    }

    override suspend fun billWiseReport(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.billWiseReport(
                DB.addParams(body)
            )
        }
    }

    override suspend fun itemsReport(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.itemsReport(
                DB.addParams(body)
            )
        }
    }

    override suspend fun briefReport(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.briefReport(
                DB.addParams(body)
            )
        }
    }

    override suspend fun chartDaily(body: JsonObject): Resource<JsonArray> {
        return safeApiCall {
            api.chartDaily(
                DB.addParams(body)
            )
        }
    }

    override suspend fun chartWeekly(body: JsonObject): Resource<JsonArray> {
        return safeApiCall {
            api.chartWeekly(
                DB.addParams(body)
            )
        }
    }

    override suspend fun chartMonthly(body: JsonObject): Resource<JsonArray> {
        return safeApiCall {
            api.chartMonthly(
                DB.addParams(body)
            )
        }
    }

    override suspend fun chartYearly(body: JsonObject): Resource<JsonArray> {
        return safeApiCall {
            api.chartYearly(
                DB.addParams(body)
            )
        }
    }
}
