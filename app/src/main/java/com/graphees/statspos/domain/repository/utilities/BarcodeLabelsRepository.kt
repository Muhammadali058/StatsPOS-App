package com.graphees.statspos.domain.repository.utilities

import com.graphees.statspos.domain.models.utilities.BarcodeLabels
import com.graphees.statspos.utils.Resource
import com.google.gson.JsonObject

interface BarcodeLabelsRepository {
    suspend fun loadBarcodeLabels(userId: Long): Resource<JsonObject>

    suspend fun insertBarcodeLabel(barcodeLabel: BarcodeLabels): Resource<JsonObject>

    suspend fun updateBarcodeLabel(barcodeLabel: BarcodeLabels): Resource<JsonObject>

    suspend fun deleteBarcodeLabel(id: Long): Resource<JsonObject>

    suspend fun getBarcodeLabel(id: Long): Resource<JsonObject>

    suspend fun clearBarcodeLabels(userId: Long): Resource<JsonObject>

    suspend fun getBarcodeLabels(userId: Long): Resource<JsonObject>

    suspend fun getBarcodeLabelForPreview(id: Long): Resource<JsonObject>
}