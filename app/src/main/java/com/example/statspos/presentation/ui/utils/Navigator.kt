package com.example.statspos.presentation.ui.utils

import androidx.navigation3.runtime.NavKey

class Navigator(
    val state: NavigationState
) {
    fun navigate(route: NavKey){
        if(route in state.backStacks.keys){
            state.selectedRoute = route
        }else{
            state.backStacks[state.selectedRoute]?.add(route)
        }
    }

    fun goBack(){
        val currentStack = state.backStacks[state.selectedRoute]?: error("Back stack for ${state.selectedRoute} not exists")
        val currentRoute = currentStack.last()

        if(currentRoute == state.selectedRoute){
            state.selectedRoute = state.startRoute
        }else{
            currentStack.removeLastOrNull()
        }
    }
}