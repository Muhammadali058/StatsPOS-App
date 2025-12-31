package com.example.statspos.domain.repository.items

import com.example.statspos.domain.models.items.PackageItems
import com.example.statspos.domain.models.items.Packages
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface PackagesRepository {
    // region Packages
    suspend fun loadPackages(body: JsonObject): Resource<JsonObject>

    suspend fun insertPackage(packages: Packages): Resource<JsonObject>

    suspend fun updatePackage(packages: Packages): Resource<JsonObject>

    suspend fun deletePackage(id: Long): Resource<JsonObject>

    suspend fun getPackage(id: Long): Resource<JsonObject>

    suspend fun generatePackage(body: JsonObject): Resource<JsonObject>
    // endregion

    // region Package-Items
    suspend fun loadPackageItems(body: JsonObject): Resource<JsonObject>

    suspend fun insertPackageItem(packageItem: PackageItems): Resource<JsonObject>

    suspend fun updatePackageItem(packageItem: PackageItems): Resource<JsonObject>

    suspend fun deletePackageItem(id: Long): Resource<JsonObject>

    suspend fun getPackageItem(id: Long): Resource<JsonObject>
    // endregion
}