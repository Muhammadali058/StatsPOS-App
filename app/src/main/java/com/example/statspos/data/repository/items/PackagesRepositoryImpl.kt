package com.example.statspos.data.repository.items

import com.example.statspos.data.remote.items.PackagesApi
import com.example.statspos.domain.models.items.PackageItems
import com.example.statspos.domain.models.items.Packages
import com.example.statspos.domain.repository.items.PackagesRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class PackagesRepositoryImpl @Inject constructor(
    private val api: PackagesApi
) : PackagesRepository {
    // region Packages
    override suspend fun loadPackages(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadPackages(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertPackage(packages: Packages): Resource<JsonObject> {
        val body = DB.getJsonObject(packages)
        return safeApiCall {
            api.insertPackage(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updatePackage(packages: Packages): Resource<JsonObject> {
        val body = DB.getJsonObject(packages)
        return safeApiCall {
            api.updatePackage(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deletePackage(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deletePackage(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getPackage(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getPackage(
                DB.addParams(body)
            )
        }
    }

    override suspend fun generatePackage(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.generatePackage(
                DB.addParams(body)
            )
        }
    }
    // endregion

    // region Package-Items
    override suspend fun loadPackageItems(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadPackageItems(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertPackageItem(packageItem: PackageItems): Resource<JsonObject> {
        val body = DB.getJsonObject(packageItem)
        return safeApiCall {
            api.insertPackageItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updatePackageItem(packageItem: PackageItems): Resource<JsonObject> {
        val body = DB.getJsonObject(packageItem)
        return safeApiCall {
            api.updatePackageItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deletePackageItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.deletePackageItem(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getPackageItem(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getPackageItem(
                DB.addParams(body)
            )
        }
    }
// endregion
}
