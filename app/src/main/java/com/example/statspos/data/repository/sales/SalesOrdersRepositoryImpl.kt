package com.example.statspos.data.repository.sales

import com.example.statspos.data.remote.sales.SalesOrdersApi
import com.example.statspos.domain.repository.sales.SalesOrdersRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class SalesOrdersRepositoryImpl @Inject constructor(
    private val api: SalesOrdersApi
) : SalesOrdersRepository {

    override suspend fun orderValidation(jsonObject: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.orderValidation(
                DB.addParams(jsonObject)
            )
        }
    }
}