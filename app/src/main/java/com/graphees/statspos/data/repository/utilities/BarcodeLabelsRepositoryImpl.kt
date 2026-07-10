package com.graphees.statspos.data.repository.utilities

import com.graphees.statspos.data.remote.utilities.BarcodeLabelsApi
import com.graphees.statspos.domain.models.utilities.BarcodeLabels
import com.graphees.statspos.domain.repository.utilities.BarcodeLabelsRepository
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class BarcodeLabelsRepositoryImpl @Inject constructor(
    private val api: BarcodeLabelsApi
) : BarcodeLabelsRepository {
    override suspend fun loadBarcodeLabels(userId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.loadBarcodeLabels(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertBarcodeLabel(barcodeLabel: BarcodeLabels): Resource<JsonObject> {
        val body = DB.getJsonObject(barcodeLabel)
        return safeApiCall {
            api.insertBarcodeLabel(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateBarcodeLabel(barcodeLabel: BarcodeLabels): Resource<JsonObject> {
        val body = DB.getJsonObject(barcodeLabel)
        return safeApiCall {
            api.updateBarcodeLabel(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteBarcodeLabel(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.deleteBarcodeLabel(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getBarcodeLabel(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getBarcodeLabel(
                DB.addParams(body)
            )
        }
    }

    override suspend fun clearBarcodeLabels(userId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.clearBarcodeLabels(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getBarcodeLabels(userId: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.getBarcodeLabels(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getBarcodeLabelForPreview(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getBarcodeLabelForPreview(
                DB.addParams(body)
            )
        }
    }
}
