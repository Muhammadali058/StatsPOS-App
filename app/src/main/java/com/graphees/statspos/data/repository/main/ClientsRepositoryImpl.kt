package com.graphees.statspos.data.repository.main

import com.graphees.statspos.data.remote.main.ClientsApi
import com.graphees.statspos.domain.repository.main.ClientsRepository
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.Resource
import com.graphees.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import com.graphees.statspos.domain.models.main.AppSubscription
import com.graphees.statspos.utils.DB
import javax.inject.Inject

class ClientsRepositoryImpl @Inject constructor(
    private val api: ClientsApi
) : ClientsRepository {
    override suspend fun clientLogin(body: JsonObject): Resource<JsonObject> =
        safeApiCall { api.clientLogin(body) }

    override suspend fun clientSignup(body: JsonObject): Resource<JsonObject>  = safeApiCall { api.clientSignup(body) }

    override suspend fun getBranches(clientId: Int): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("clientId", clientId)
        }

        return safeApiCall {
            api.getBranches(
                body
            )
        }
    }

    override suspend fun getClient(clientId: Int): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", clientId)
        }

        return safeApiCall {
            api.getClient(
                body
            )
        }
    }

    override suspend fun getClientData(clientId: Int): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", clientId)
        }

        return safeApiCall {
            api.getClientData(
                body
            )
        }
    }

    override suspend fun updateShoppingAppFCMToken(fcmToken:String): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", HP.clientId)
            addProperty("fcmToken", fcmToken)
        }

        return safeApiCall {
            api.updateFCMToken(
                body
            )
        }
    }

    override suspend fun paymentRequest(body: JsonObject): Resource<JsonObject> {
        body.addProperty("id", HP.appSubscription.id!!)
        body.addProperty("clientId", HP.clientId)
        body.addProperty("paymentRequest", true)

        return safeApiCall {
            api.paymentRequest(body)
        }
    }

    override suspend fun updateAppSubscription(appSubscription: AppSubscription): Resource<JsonObject> {
        val body = DB.getJsonObject(appSubscription)
        return safeApiCall {
            api.updateAppSubscription(body)
        }
    }
}
