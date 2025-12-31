package com.example.statspos.data.repository.reports

import com.example.statspos.data.remote.reports.AccountReportsApi
import com.example.statspos.domain.repository.reports.AccountReportsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class AccountReportsRepositoryImpl @Inject constructor(
    private val api: AccountReportsApi
) : AccountReportsRepository {
    override suspend fun ledger(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.ledger(
                DB.addParams(body)
            )
        }
    }

    override suspend fun receipts(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.receipts(
                DB.addParams(body)
            )
        }
    }

    override suspend fun payments(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.payments(
                DB.addParams(body)
            )
        }
    }

    override suspend fun expenses(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.expenses(
                DB.addParams(body)
            )
        }
    }

    override suspend fun incomeStatement(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.incomeStatement(
                DB.addParams(body)
            )
        }
    }

    override suspend fun cashAccount(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.cashAccount(
                DB.addParams(body)
            )
        }
    }

    override suspend fun debtors(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.debtors(
                DB.addParams(body)
            )
        }
    }

    override suspend fun creditors(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.creditors(
                DB.addParams(body)
            )
        }
    }

    override suspend fun customersBalanceList(body: JsonObject): Resource<JsonObject> {
        return safeApiCall {
            api.customersBalanceList(
                DB.addParams(body)
            )
        }
    }
}
