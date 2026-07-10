package com.graphees.statspos.presentation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.graphees.statspos.domain.models.main.Clients
import com.graphees.statspos.presentation.ui.screens.main.login.ClientLoginScreen
import com.graphees.statspos.presentation.ui.screens.main.login.ClientSignupScreen
import com.graphees.statspos.presentation.ui.screens.main.login.CloseAppScreen
import com.graphees.statspos.presentation.ui.screens.main.login.LoginScreen
import com.graphees.statspos.presentation.ui.screens.main.main.MainScreen
import com.graphees.statspos.presentation.ui.screens.main.main.SplashScreen
import com.graphees.statspos.presentation.viewmodels.main.ClientsViewModel
import com.graphees.statspos.presentation.viewmodels.main.LocalDataViewModel
import com.graphees.statspos.presentation.viewmodels.main.LoginViewModel
import com.graphees.statspos.utils.DB
import com.graphees.statspos.utils.HP
import com.graphees.statspos.utils.SocketManager
import kotlinx.coroutines.flow.first
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
    data class Login(val remember: Boolean, val username: String?, val password: String?) :
        Screens()

    @Serializable
    data object Main : Screens()

    @Serializable
    data object CloseApp : Screens()

}

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    val backStack = rememberNavBackStack(Screens.Splash)
    val viewModel = hiltViewModel<LocalDataViewModel>()
    val clientsViewModel = hiltViewModel<ClientsViewModel>()
    val loginViewModel = hiltViewModel<LoginViewModel>()
    val clientsState by clientsViewModel.state.collectAsStateWithLifecycle()

    fun navigate(key: NavKey) {
        if (backStack.lastOrNull() != key) {
            backStack.add(key)
            backStack.removeFirstOrNull()
        }
    }

    LaunchedEffect(Unit) {
        val clientId = viewModel.getClientId().first()
        val isOnline = viewModel.getIsOnline().first()
        val branches = viewModel.getBranches().first()
        val baseUrl = viewModel.getBaseUrl().first()

        if (clientId != 0) {
            viewModel.getLoginInfo { remember, username, password ->
                HP.clientId = clientId
                DB.isOnlineMode = isOnline
                DB.branches = branches

                if(!isOnline){
                    HP.clientId = 1
                    DB.setBaseUrl(baseUrl)
                    DB.setWebSocketUrl(baseUrl)
                }

                navigate(Screens.Login(remember, username, password))
            }
        } else {
            navigate(Screens.ClientLogin)
        }
    }

    val activity = LocalActivity.current as Activity
    BackHandler {
        if (backStack.size == 1) {
            activity.finish()
        }
    }

    fun showLoginScreen(client: Clients){
        HP.clientId = client.id!!
        DB.isOnlineMode = client.isOnline!!

        if(!client.isOnline!!){
            scope.launch {
                val baseUrl = viewModel.getBaseUrl().first()

                HP.clientId = 1
                DB.setBaseUrl(baseUrl)

                navigate(Screens.CloseApp)
            }
        }else{
            navigate(Screens.Login(false, "", ""))
        }
    }

    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Screens.Splash> {
                SplashScreen()
            }
            entry<Screens.ClientLogin> {
                ClientLoginScreen(
                    viewModel = clientsViewModel,
                    onClientLogin = { client ->
                        showLoginScreen(client)
                    },
                    onSignup = {
                        backStack.removeLastOrNull()
                        backStack.add(Screens.ClientSignup)
//                        backStack.removeFirstOrNull()
                    },
                )
            }
            entry<Screens.ClientSignup> {
                ClientSignupScreen(
                    viewModel = clientsViewModel,
                    onSignup = { client ->
                        loginViewModel.onUsernameChange(clientsState.username)
                        loginViewModel.onPasswordChange(clientsState.password)

                        SocketManager.init()
                        SocketManager.connect()

                        HP.clientId = client.id!!
                        DB.isOnlineMode = client.isOnline!!

                        loginViewModel.login { success ->
                            if (HP.appSettings.onlinePrints == true) {
                                SocketManager.join()
                            }
                            navigate(Screens.Main)
                        }
                    },
                    onSignIn = {
                        backStack.removeLastOrNull()
                        backStack.add(Screens.ClientLogin)
                    }
                )
            }
            entry<Screens.Login> { key ->
                LoginScreen(
                    viewModel = loginViewModel,
                    remember = key.remember,
                    username = key.username,
                    password = key.password,
                    onLogin = {
                        navigate(Screens.Main)
                    },
                    onReset = {
                        scope.launch {
                            viewModel.setClientId(0)
                            backStack.add(Screens.CloseApp)
                        }
                    },
                    onBrachChange = {
                        backStack.add(Screens.CloseApp)
                        backStack.removeFirstOrNull()
                    },
                )
            }
            entry<Screens.Main> {
                MainScreen(
                    onLogout = {
                        scope.launch {
                            viewModel.getLoginInfo { remember, username, password ->
                                navigate(Screens.Login(remember, username, password))
                            }
                        }
                    }
                )
            }
            entry<Screens.CloseApp> {
                CloseAppScreen()
            }
        }
    )
}

