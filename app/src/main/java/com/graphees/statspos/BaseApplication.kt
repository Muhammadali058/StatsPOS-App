package com.graphees.statspos

import android.app.Application
import com.graphees.statspos.utils.LocalDataStore
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent

@HiltAndroidApp
class BaseApplication: Application(){
    override fun onCreate() {
        super.onCreate()

        PDFBoxResourceLoader.init(this)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SettingsProviderEntryPoint {
    fun localDataStore(): LocalDataStore
}