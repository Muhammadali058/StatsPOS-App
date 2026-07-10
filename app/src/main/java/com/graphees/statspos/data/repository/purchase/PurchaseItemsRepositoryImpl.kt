package com.graphees.statspos.data.repository.purchase

import com.graphees.statspos.data.remote.purchase.PurchaseItemsApi
import com.graphees.statspos.domain.models.purchase.PurchaseItems
import com.graphees.statspos.domain.repository.purchase.PurchaseItemsRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class PurchaseItemsRepositoryImpl @Inject constructor(
    private val api: PurchaseItemsApi
) : PurchaseItemsRepository {

    override suspend fun loadPurchaseItems(body: JsonObject): Resource<JsonObject> {
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.loadPurchaseItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertPurchaseItem(purchaseItem: PurchaseItems): Resource<JsonObject> {
        val body = DB.getJsonObject(purchaseItem)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.insertPurchaseItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updatePurchaseItem(purchaseItem: PurchaseItems): Resource<JsonObject> {
        val body = DB.getJsonObject(purchaseItem)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.updatePurchaseItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deletePurchaseItem(id: Long, isPostedBill: Boolean): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("isPostedBill", isPostedBill)
        }
        return safeApiCall {
            api.deletePurchaseItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getPurchaseItem(id: Long, isPostedBill: Boolean): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("isPostedBill", isPostedBill)
        }
        return safeApiCall {
            api.getPurchaseItem(
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
