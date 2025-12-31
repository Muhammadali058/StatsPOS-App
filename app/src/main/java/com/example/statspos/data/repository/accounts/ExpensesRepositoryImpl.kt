package com.example.statspos.data.repository.accounts

import com.example.statspos.data.remote.accounts.ExpensesApi
import com.example.statspos.domain.models.accounts.Accounts
import com.example.statspos.domain.models.accounts.Expenses
import com.example.statspos.domain.repository.accounts.ExpensesRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class ExpensesRepositoryImpl @Inject constructor(
    private val api: ExpensesApi
) : ExpensesRepository {
    // region Expenses
    override suspend fun loadExpenses(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadExpenses(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertExpense(expense: Expenses): Resource<JsonObject> {
        val body = DB.getJsonObject(expense)
        return safeApiCall {
            api.insertExpense(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateExpense(expense: Expenses): Resource<JsonObject> {
        val body = DB.getJsonObject(expense)
        return safeApiCall {
            api.updateExpense(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteExpense(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteExpense(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getExpense(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getExpense(
                DB.addParams(body)
            )
        }
    }
    // endregion

    // region Sub-Expenses
    override suspend fun loadSubExpenses(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.loadSubExpenses(
                DB.addParams(body)
            )
        }
    }

    override suspend fun insertSubExpense(subExpense: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(subExpense)
        return safeApiCall {
            api.insertSubExpense(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateSubExpense(subExpense: Accounts): Resource<JsonObject> {
        val body = DB.getJsonObject(subExpense)
        return safeApiCall {
            api.updateSubExpense(
                DB.addParams(body)
            )
        }
    }

    override suspend fun deleteSubExpense(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
            addProperty("userId", HP.user.id)
        }
        return safeApiCall {
            api.deleteSubExpense(
                DB.addParams(body)
            )
        }
    }

    override suspend fun getSubExpense(id: Long): Resource<JsonObject> {
        val body = JsonObject().apply {
            addProperty("id", id)
        }
        return safeApiCall {
            api.getSubExpense(
                DB.addParams(body)
            )
        }
    }
    // endregion
}
