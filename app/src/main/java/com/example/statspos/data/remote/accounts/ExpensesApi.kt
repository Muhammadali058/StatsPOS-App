package com.example.statspos.data.remote.accounts

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ExpensesApi {
    // region Expenses
    @POST("expenses/loadExpenses")
    suspend fun loadExpenses(@Body body: JsonObject): Response<JsonObject>

    @POST("expenses/insertExpense")
    suspend fun insertExpense(@Body body: JsonObject): Response<JsonObject>

    @POST("expenses/updateExpense")
    suspend fun updateExpense(@Body body: JsonObject): Response<JsonObject>

    @POST("expenses/deleteExpense")
    suspend fun deleteExpense(@Body body: JsonObject): Response<JsonObject>

    @POST("expenses/getExpense")
    suspend fun getExpense(@Body body: JsonObject): Response<JsonObject>
    // endregion

    // region Sub-Expenses
    @POST("subExpenses/loadSubExpenses")
    suspend fun loadSubExpenses(@Body body: JsonObject): Response<JsonObject>

    @POST("subExpenses/insertSubExpense")
    suspend fun insertSubExpense(@Body body: JsonObject): Response<JsonObject>

    @POST("subExpenses/updateSubExpense")
    suspend fun updateSubExpense(@Body body: JsonObject): Response<JsonObject>

    @POST("subExpenses/deleteSubExpense")
    suspend fun deleteSubExpense(@Body body: JsonObject): Response<JsonObject>

    @POST("subExpenses/getSubExpense")
    suspend fun getSubExpense(@Body body: JsonObject): Response<JsonObject>
    // endregion
}