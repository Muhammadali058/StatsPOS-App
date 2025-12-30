package com.example.statspos.data.repository.items

import com.example.statspos.data.remote.items.CategoriesApi
import com.example.statspos.domain.models.items.Categories
import com.example.statspos.domain.models.items.SubCategories
import com.example.statspos.domain.repository.items.CategoriesRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import javax.inject.Inject

class CategoriesRepositoryImpl @Inject constructor(
    private val api: CategoriesApi
) : CategoriesRepository {

    // region Categories
    override suspend fun loadCategories(body: JsonObject): Resource<JsonObject> {
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.loadCategories(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertCategory(category: Categories): Resource<JsonObject> {
        val body = DB.getJsonObject(category)
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.insertCategory(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateCategory(category: Categories): Resource<JsonObject> {
        val body = DB.getJsonObject(category)
        body.addProperty("branchGroupId", HP.branchGroupId)

        return safeApiCall {
            api.updateCategory(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteCategory(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("branchGroupId", HP.branchGroupId)
            addProperty("id", id)
            addProperty("userId", HP.user.id ?: 0)
        }

        return safeApiCall {
            api.deleteCategory(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getCategory(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }

        return safeApiCall {
            api.getCategory(
                DB.addParams(body)
            )
        }
    }
    // endregion

    // region Sub-Categories
    override suspend fun loadSubCategories(body: JsonObject): Resource<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun insertSubCategory(subCategory: SubCategories): Resource<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun updateSubCategory(subCategory: SubCategories): Resource<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSubCategory(id: Long): Resource<JsonObject> {
        TODO("Not yet implemented")
    }

    override suspend fun getSubCategory(id: Long): Resource<JsonObject> {
        TODO("Not yet implemented")
    }
    // endregion

//    override suspend fun uploadImage(
//        image: MultipartBody.Part
//    ): Resource<JsonObject> {
//        val body = DB.addParams(JsonObject())
//
//        return safeApiCall {
//            api.uploadImage(
//                image = image,
//                body = MultipartBody.Part.createFormData("data", body.toString())
//            )
//        }
//    }
}
