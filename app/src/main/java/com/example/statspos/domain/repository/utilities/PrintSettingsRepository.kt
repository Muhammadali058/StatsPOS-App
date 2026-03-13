package com.example.statspos.domain.repository.utilities

import com.example.statspos.domain.models.utilities.settings.PrintSettings
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface PrintSettingsRepository {
    suspend fun updatePrintSettings(printSettings: PrintSettings): Resource<JsonObject>
}