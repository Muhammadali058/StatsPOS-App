package com.example.statspos.data.repository.purchase

import com.example.statspos.data.remote.purchase.PurchaseApi
import com.example.statspos.domain.models.purchase.Purchase
import com.example.statspos.domain.repository.purchase.PurchaseRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class PurchaseRepositoryImpl @Inject constructor(
    private val api: PurchaseApi
) : PurchaseRepository {
    override suspend fun insertPurchase(purchase: Purchase): Resource<JsonObject> {
        val body = DB.getJsonObject(purchase)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.insertPurchase(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updatePurchase(purchase: Purchase): Resource<JsonObject> {
        val body = DB.getJsonObject(purchase)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.updatePurchase(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deletePurchase(id: Long, isPostedBill: Boolean): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
            addProperty("isPostedBill", isPostedBill)
        }
        return safeApiCall {
            api.deletePurchase(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getPurchase(id: Long, isPostedBill: Boolean): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
            addProperty("isPostedBill", isPostedBill)
        }
        return safeApiCall {
            api.getPurchase(
                DB.addParams(body)
            )
        }
    }

    override suspend fun tempClose(purchase: Purchase): Resource<JsonObject> {
        val body = DB.getJsonObject(purchase)
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.tempClose(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getInvoiceId(): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.getInvoiceId(
                DB.addParams(body)
            )
        }
    }

    override suspend fun isReplaceExists(vendorId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("vendorId",vendorId)
        }
        return safeApiCall {
            api.isReplaceExists(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getBill(body: JsonObject): Resource<JsonObject> {
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.getBill(
                DB.addParams(body)
            )
        }
    }

    override suspend fun generateBarcodeLabels(body: JsonObject): Resource<JsonObject> {
        body.addProperty("userId", HP.user.id)

        return safeApiCall {
            api.generateBarcodeLabels(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadPendingBills(): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.loadPendingBills(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadPendingBillItems(purchaseId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("purchaseId", purchaseId)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.loadPendingBillItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadPostedBills(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadPostedBills(
                DB.addParams(body)
            )
        }
    }

    override suspend fun loadPostedBillItems(purchaseId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("purchaseId", purchaseId)
        }
        return safeApiCall {
            api.loadPostedBillItems(
                DB.addParams(body)
            )
        }
    }
}
