package com.example.statspos.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.presentation.ui.screens.main.ClientLoginScreen
import com.example.statspos.presentation.ui.screens.main.ClientSignupScreen
import com.example.statspos.presentation.ui.screens.main.LoginScreen
import com.example.statspos.presentation.ui.screens.main.SplashScreen
import com.example.statspos.presentation.viewmodels.main.LocalDataViewModel
import com.example.statspos.utils.DB
import com.example.statspos.utils.showToast
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

private sealed class Screens : NavKey {
    @Serializable
    data object Splash : Screens()

    @Serializable
    data object ClientLogin : Screens()

    @Serializable
    data object ClientSignup : Screens()

    @Serializable
    data class Login(val clientId: Long) : Screens()

    @Serializable
    data object Main : Screens()
}

@Preview(showBackground = true)
@Composable
fun App() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backStack = rememberNavBackStack(Screens.Splash)
    val viewModel = hiltViewModel<LocalDataViewModel>()

    LaunchedEffect(true) {
        if (DB.IS_ONLINE_MODE) {
            viewModel.getClientId().collect { clientId ->
                if (clientId != 0.toLong()) {
                    backStack.add(Screens.Login(clientId))
                    backStack.removeFirstOrNull()
                } else {
                    backStack.add(Screens.ClientLogin)
                    backStack.removeFirstOrNull()
                }
            }
        } else {
            viewModel.getLocalClientId().collect { localClientId ->
                if (localClientId != 0) {
                    viewModel.getBaseUrl().collect { baseUrl ->
                        if (baseUrl != null) {
                            DB.setBaseUrl(baseUrl)

                            backStack.add(Screens.Login(1))
                            backStack.removeFirstOrNull()
                        } else {
                            backStack.add(Screens.ClientLogin)
                            backStack.removeFirstOrNull()
                        }
                    }
                } else {
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
                            if (localClientId != 0 && baseUrl.isNotEmpty()) {
                                viewModel.setLocalClientId(localClientId)
                                viewModel.setBaseUrl(baseUrl)

                                context.showToast("Open App again to continue")
                                backStack.removeLastOrNull()
                            }
                        }
                    },
                    onSignup = {
                        backStack.add(Screens.ClientSignup)
//                        backStack.removeFirstOrNull()
                    }
                )
            }
            entry<Screens.ClientSignup> {
                ClientSignupScreen (
                    onSignup = { clientId ->
                        scope.launch {
                            viewModel.setClientId(clientId)

                            backStack.add(Screens.Login(clientId))
                            backStack.removeFirstOrNull()
                        }
                    }
                )
            }
            entry<Screens.Login> { key ->
                LoginScreen(clientId = key.clientId) {
                    backStack.add(Screens.Main)
                    backStack.removeFirstOrNull()
                }
            }
            entry<Screens.Main> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "Main Screen"
                    )
                }
            }
        }
    )

}
