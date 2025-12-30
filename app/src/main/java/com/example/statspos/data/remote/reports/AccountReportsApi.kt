package com.example.statspos.data.remote.reports

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountReportsApi {
    @POST("accountReports/ledger")
    suspend fun ledger(@Body body: JsonObject): Response<JsonObject>

    @POST("accountReports/receipts")
    suspend fun receipts(@Body body: JsonObject): Response<JsonObject>

    @POST("accountReports/payments")
    suspend fun payments(@Body body: JsonObject): Response<JsonObject>

    @POST("accountReports/expenses")
    suspend fun expenses(@Body body: JsonObject): Response<JsonObject>

    @POST("accountReports/incomeStatement")
    suspend fun incomeStatement(@Body body: JsonObject): Response<JsonObject>

    @POST("accountReports/cashAccount")
    suspend fun cashAccount(@Body body: JsonObject): Response<JsonObject>

    @POST("accountReports/debtors")
    suspend fun debtors(@Body body: JsonObject): Response<JsonObject>

    @POST("accountReports/creditors")
    suspend fun creditors(@Body body: JsonObject): Response<JsonObject>

    @POST("accountReports/customersBalanceList")
    suspend fun customersBalanceList(@Body body: JsonObject): Response<JsonObject>

}