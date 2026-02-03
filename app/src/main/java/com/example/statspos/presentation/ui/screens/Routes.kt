package com.example.statspos.presentation.ui.screens

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class BottomRoutes : NavKey{
    @Serializable
    data object Home : BottomRoutes()

    @Serializable
    data object Items : BottomRoutes()

    @Serializable
    data object Sales : BottomRoutes()

    @Serializable
    data object Reports : BottomRoutes()

}

sealed class TopRoutes : NavKey{
    @Serializable
    data object Home : TopRoutes()

    @Serializable
    data object SearchItem : TopRoutes()

    @Serializable
    data class AddUpdateItem(val updateId: Long, val isUpdate: Boolean) : TopRoutes()

    @Serializable
    data object Purchase : TopRoutes()

    @Serializable
    data object Categories : TopRoutes()

    @Serializable
    data object AddSales : BottomRoutes()
}