package com.example.statspos.data.repository.utilities

import com.example.statspos.data.remote.utilities.BarcodeLabelsApi
import com.example.statspos.domain.models.utilities.BarcodeLabels
import com.example.statspos.domain.repository.utilities.BarcodeLabelsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
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
