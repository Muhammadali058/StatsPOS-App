package com.example.statspos.presentation

import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.presentation.ui.screens.SplashScreen
import com.example.statspos.presentation.viewmodels.CategoriesViewModel

@Composable
fun App() {
    val viewModel = hiltViewModel<CategoriesViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state) {
        when{
            state.isLoading ->{
                Log.d("TAG Loading", state.isLoading.toString())
            }
            state.infoMessage != null ->{
                Log.d("TAG InfoMessage", state.infoMessage.toString())
            }
            state.error != null ->{
                Log.d("TAG Error", state.error.toString())
            }
            state.categories.isNotEmpty() ->{
                Log.d("TAG Success", state.categories.toString())
            }
        }
    }

    if(state.isLoading){
        CircularProgressIndicator()
    }else {
        SplashScreen()
    }
}