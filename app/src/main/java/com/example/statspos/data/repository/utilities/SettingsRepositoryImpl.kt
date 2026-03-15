package com.example.statspos.data.repository.utilities

import com.example.statspos.data.remote.utilities.SettingsApi
import com.example.statspos.domain.models.utilities.settings.AdminPasswords
import com.example.statspos.domain.models.utilities.settings.AdminSettings
import com.example.statspos.domain.models.utilities.settings.AppSettings
import com.example.statspos.domain.models.utilities.settings.Passwords
import com.example.statspos.domain.models.utilities.settings.PrintSettings
import com.example.statspos.domain.models.utilities.settings.Settings
import com.example.statspos.domain.repository.utilities.SettingsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val api: SettingsApi
) : SettingsRepository {
    override suspend fun updateSettings(settings: Settings, passwords: Passwords): Resource<JsonObject> {
        val body = JsonObject().apply {
            add("settings", DB.getJsonObject(settings))
            add("passwords", DB.getJsonObject(passwords))
            add("billSettings", JsonObject())
        }

        return safeApiCall {
            api.updateSettings(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateAdminSettings(adminSettings: AdminSettings, adminPasswords: AdminPasswords): Resource<JsonObject> {
        val body = JsonObject().apply {
            add("adminSettings", DB.getJsonObject(adminSettings))
            add("adminPasswords", DB.getJsonObject(adminPasswords))
        }

        return safeApiCall {
            api.updateAdminSettings(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updatePrintSettings(printSettings: PrintSettings): Resource<JsonObject> {
        val body = DB.getJsonObject(printSettings)

        return safeApiCall {
            api.updatePrintSettings(
                DB.addParams(body)
            )
        }
    }

    override suspend fun updateAppSettings(appSettings: AppSettings): Resource<JsonObject> {
        val body = DB.getJsonObject(appSettings)

        return safeApiCall {
            api.updateAppSettings(
                DB.addParams(body)
            )
        }
    }
}
