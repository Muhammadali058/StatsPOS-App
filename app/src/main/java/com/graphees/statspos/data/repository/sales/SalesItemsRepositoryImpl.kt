package com.graphees.statspos.data.repository.sales

import com.graphees.statspos.data.remote.sales.SalesItemsApi
import com.graphees.statspos.domain.models.sales.SalesItems
import com.graphees.statspos.domain.repository.sales.SalesItemsRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class SalesItemsRepositoryImpl @Inject constructor(
    private val api: SalesItemsApi
) : SalesItemsRepository {
    override suspend fun loadSalesItems(body: JsonObject): Resource<JsonObject> {
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.loadSalesItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertSalesItem(salesItem: SalesItems): Resource<JsonObject> {
        val body = DB.getJsonObject(salesItem)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.insertSalesItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateSalesItem(salesItem: SalesItems): Resource<JsonObject> {
        val body = DB.getJsonObject(salesItem)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.updateSalesItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteSalesItem(id: Long, isPostedBill: Boolean): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("isPostedBill", isPostedBill)
        }
        return safeApiCall {
            api.deleteSalesItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getSalesItem(id: Long, isPostedBill: Boolean): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("isPostedBill", isPostedBill)
        }
        return safeApiCall {
            api.getSalesItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun isBarcodeExists(body: JsonObject): Resource<JsonObject> {
        body.addProperty("branchGroupId", HP.branchGroupId)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.isBarcodeExists(
                DB.addParams(body)
            )
        }
    }
}
