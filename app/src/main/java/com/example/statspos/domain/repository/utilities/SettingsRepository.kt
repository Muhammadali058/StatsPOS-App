package com.example.statspos.domain.repository.utilities

import com.example.statspos.domain.models.utilities.settings.AdminPasswords
import com.example.statspos.domain.models.utilities.settings.AdminSettings
import com.example.statspos.domain.models.utilities.settings.AppSettings
import com.example.statspos.domain.models.utilities.settings.Passwords
import com.example.statspos.domain.models.utilities.settings.PrintSettings
import com.example.statspos.domain.models.utilities.settings.Settings
import com.example.statspos.utils.Resource
import com.google.gson.JsonObject

interface SettingsRepository {
    suspend fun updateSettings(settings: Settings, passwords: Passwords): Resource<JsonObject>

    suspend fun updateAdminSettings(adminSettings: AdminSettings, adminPasswords: AdminPasswords): Resource<JsonObject>

    suspend fun updatePrintSettings(printSettings: PrintSettings): Resource<JsonObject>

    suspend fun updateAppSettings(appSettings: AppSettings): Resource<JsonObject>
}