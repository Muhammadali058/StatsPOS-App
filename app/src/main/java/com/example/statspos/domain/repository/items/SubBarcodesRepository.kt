package com.example.statspos.domain.repository.items

import com.example.statspos.domain.models.items.SubBarcodes
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface SubBarcodesRepository {
    suspend fun loadSubBarcodes(body: JsonObject): Resource<JsonObject>

    suspend fun insertSubBarcode(subBarcode: SubBarcodes): Resource<JsonObject>

    suspend fun updateSubBarcode(subBarcode: SubBarcodes): Resource<JsonObject>

    suspend fun deleteSubBarcode(id: Long): Resource<JsonObject>

    suspend fun getSubBarcode(id: Long): Resource<JsonObject>
}