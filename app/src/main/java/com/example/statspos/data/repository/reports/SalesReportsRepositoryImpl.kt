package com.example.statspos.data.repository.reports

import com.example.statspos.data.remote.reports.SalesReportsApi
import com.example.statspos.domain.repository.reports.SalesReportsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class SalesReportsRepositoryImpl @Inject constructor(
    private val api: SalesReportsApi
) : SalesReportsRepository {
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

    override suspend fun chartDaily(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.chartDaily(
                DB.addParams(body)
            )
        }
    }

    override suspend fun chartWeekly(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.chartWeekly(
                DB.addParams(body)
            )
        }
    }

    override suspend fun chartMonthly(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.chartMonthly(
                DB.addParams(body)
            )
        }
    }

    override suspend fun chartYearly(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.chartYearly(
                DB.addParams(body)
            )
        }
    }
}
