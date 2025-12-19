package com.example.statspos.presentation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.presentation.ui.screens.main.ClientLoginScreen
import com.example.statspos.presentation.ui.screens.main.SplashScreen
import com.example.statspos.presentation.viewmodels.main.ClientsViewModel
import com.example.statspos.presentation.viewmodels.main.LocalDataViewModel
import com.example.statspos.utils.DB
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

private sealed class Screens : NavKey {
    @Serializable
    data object Splash : Screens()
    @Serializable
    data object ClientLogin : Screens()

    @Serializable
    data class Login(val clientId: Long) : Screens()
}

@Preview(showBackground = true)
@Composable
fun App() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backStack = rememberNavBackStack(Screens.Splash)
    val viewModel = hiltViewModel<LocalDataViewModel>()

    LaunchedEffect(true) {
        if(DB.IS_ONLINE_MODE){
            viewModel.getClientId().collect { clientId ->
                if (clientId != 0.toLong()) {
                    backStack.add(Screens.Login(clientId))
                    backStack.removeFirstOrNull()
                }else{
                    backStack.add(Screens.ClientLogin)
                    backStack.removeFirstOrNull()
                }
            }
        }else{
            viewModel.getLocalClientId().collect { localClientId ->
                if (localClientId != 0) {
                    viewModel.getBaseUrl().collect { baseUrl ->
                        if(baseUrl != null){
                            DB.setBaseUrl(baseUrl)

                            Log.d("TAG localClientId", localClientId.toString())
                            Log.d("TAG baseUrl", baseUrl)

                            backStack.add(Screens.Login(1))
                            backStack.removeFirstOrNull()
                        }else{
                            backStack.add(Screens.ClientLogin)
                            backStack.removeFirstOrNull()
                        }
                    }
                }else{
                    backStack.add(Screens.ClientLogin)
                    backStack.removeFirstOrNull()
                }
            }
        }
    }

    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<Screens.Splash> {
                SplashScreen()
            }
            entry<Screens.ClientLogin> {
                ClientLoginScreen(
                    onClientLogin = { clientId ->
                        scope.launch {
                            viewModel.setClientId(clientId)
                            backStack.add(Screens.Login(clientId))
                            backStack.removeFirstOrNull()
                        }
                    },
                    onLocalClientLogin = { localClientId, baseUrl ->
                        scope.launch {
                            if(localClientId != 0 && baseUrl.isNotEmpty()) {
                                viewModel.setLocalClientId(localClientId)
                                viewModel.setBaseUrl(baseUrl)

                                backStack.removeLastOrNull()
                            }
                        }
                    }
                )
            }
            entry<Screens.Login> { key ->
                val viewModel = hiltViewModel<ClientsViewModel>()
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Login Screen for client ${key.clientId}")
                }
            }
        }
    )

}
