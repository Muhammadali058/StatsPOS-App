package com.example.statspos.data.repository.utilities

import com.example.statspos.data.remote.utilities.PrintSettingsApi
import com.example.statspos.data.remote.utilities.SettingsApi
import com.example.statspos.domain.models.utilities.settings.AdminPasswords
import com.example.statspos.domain.models.utilities.settings.AdminSettings
import com.example.statspos.domain.models.utilities.settings.Passwords
import com.example.statspos.domain.models.utilities.settings.PrintSettings
import com.example.statspos.domain.models.utilities.settings.Settings
import com.example.statspos.domain.repository.utilities.PrintSettingsRepository
import com.example.statspos.domain.repository.utilities.SettingsRepository
import com.example.statspos.utils.DB
import com.example.statspos.utils.Resource
import com.example.statspos.utils.safeApiCall
import com.google.gson.JsonObject
import javax.inject.Inject

class PrintSettingsRepositoryImpl @Inject constructor(
    private val api: PrintSettingsApi
) : PrintSettingsRepository {
    override suspend fun updatePrintSettings(printSettings: PrintSettings): Resource<JsonObject> {
        val body = DB.getJsonObject(printSettings)

        return safeApiCall {
            api.updatePrintSettings(
                DB.addParams(body)
            )
        }
    }
}
