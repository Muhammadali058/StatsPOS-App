package com.example.statspos.domain.repository.items

import com.example.statspos.domain.models.items.Items
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface ItemsRepository {
    suspend fun loadItems(body: JsonObject): Resource<JsonObject>

    suspend fun insertItem(item: Items): Resource<JsonObject>

    suspend fun updateItem(item: Items): Resource<JsonObject>

    suspend fun deleteItem(id: Long): Resource<JsonObject>

    suspend fun getItem(id: Long): Resource<JsonObject>

    suspend fun isBarcodeExists(barcode: String): Resource<JsonObject>

    suspend fun getBarcode(): Resource<JsonObject>

}