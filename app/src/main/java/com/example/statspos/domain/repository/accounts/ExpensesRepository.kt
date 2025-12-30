package com.example.statspos.domain.repository.accounts

import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.domain.models.accounts.Expenses
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface ExpensesRepository{
    // region Expenses
    suspend fun loadExpenses(body: JsonObject): Resource<JsonObject>

    suspend fun insertExpense(expense: Expenses): Resource<JsonObject>

    suspend fun updateExpense(expense: Expenses): Resource<JsonObject>

    suspend fun deleteExpense(id: Long): Resource<JsonObject>

    suspend fun getExpense(id: Long): Resource<JsonObject>
    // endregion

    // region Sub-Expenses
    suspend fun loadSubExpenses(body: JsonObject): Resource<JsonObject>

    suspend fun insertSubExpense(subExpense: Accounts): Resource<JsonObject>

    suspend fun updateSubExpense(subExpense: Accounts): Resource<JsonObject>

    suspend fun deleteSubExpense(id: Long): Resource<JsonObject>

    suspend fun getSubExpense(id: Long): Resource<JsonObject>
    // endregion
}