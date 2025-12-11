package com.example.statspos

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.screens.main.ClientLoginScreen
import com.example.statspos.ui.theme.StatsPOSTheme
import com.example.statspos.viewmodels.CategoriesViewModel
import com.example.statspos.viewmodels.LocalDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

private sealed class Screens: NavKey{
    @Serializable
    data object ClientLogin: Screens()
    @Serializable
    data class Login(val clientId: Int): Screens()
}

@Composable
fun App() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backStack = rememberNavBackStack(Screens.ClientLogin)
    val viewModel = hiltViewModel<LocalDataViewModel>()

//    val clientId by viewModel.getClientId().collectAsStateWithLifecycle(0)
    LaunchedEffect(true) {
        viewModel.getClientId().collect {
            Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    StatsPOSTheme {
        Scaffold { innerPadding ->
            NavDisplay(
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<Screens.ClientLogin>{
                        ClientLoginScreen (
                            modifier = Modifier
                                .padding(innerPadding),
                            onLogin = {
                                scope.launch {
//                                    login(dataStore, backStack)
                                    viewModel.setClientId(1)
                                    backStack.add(Screens.Login(1))
                                    backStack.remove(Screens.ClientLogin)
                                }
                            }
                        )
                    }
                    entry<Screens.Login>{ key ->
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            Text("Login Screen ${key.clientId}")
                        }
                    }
                }
            )
        }
    }
}

//private suspend fun login(dataStore: LocalDataStore, backStack: NavBackStack<NavKey>){
//    dataStore.setClientId(1)
//    backStack.add(Screens.Login(1))
////    backStack.remove(Screens.ClientLogin)
//}