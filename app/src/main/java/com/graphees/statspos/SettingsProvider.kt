
package com.graphees.statspos

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SettingsProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun call(
        method: String,
        arg: String?,
        extras: Bundle?
    ): Bundle {

        val localDataStore =
            dagger.hilt.android.EntryPointAccessors.fromApplication(
                requireNotNull(context).applicationContext,
                SettingsProviderEntryPoint::class.java
            ).localDataStore()

        return runBlocking(Dispatchers.IO) {
            when (method) {
                // READ SETTINGS
                "getSettings" -> {
                    Bundle().apply {
                        putInt(
                            "clientId",
                            localDataStore.getClientId().first()
                        )
                        putBoolean(
                            "isOnline",
                            localDataStore.getIsOnline().first()
                        )
                    }
                }

                // UPDATE SETTINGS
                "updateSettings" -> {
                    extras?.let { data ->
                        if (data.containsKey("clientId")) {
                            localDataStore.setClientId(
                                data.getInt("clientId")
                            )
                        }
                        if (data.containsKey("isOnline")) {
                            localDataStore.setIsOnline(
                                data.getBoolean("isOnline")
                            )
                        }
                        if (data.containsKey("baseUrl")) {
                            localDataStore.setBaseUrl(
                                data.getString("baseUrl", "")
                            )
                        }
                    }

                    Bundle().apply {
                        putBoolean("success", true)
                    }
                }

                else -> {
                    Bundle().apply {
                        putBoolean("success", false)
                    }
                }
            }
        }
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String?>?
    ) = 0

    override fun getType(uri: Uri): String? = null

    override fun insert(
        uri: Uri,
        values: ContentValues?
    ): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<out String?>?,
        selection: String?,
        selectionArgs: Array<out String?>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String?>?
    ) = 0
}
