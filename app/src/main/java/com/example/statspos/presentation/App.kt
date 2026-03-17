package com.example.statspos.presentation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.presentation.ui.screens.main.login.ClientLoginScreen
import com.example.statspos.presentation.ui.screens.main.login.ClientSignupScreen
import com.example.statspos.presentation.ui.screens.main.login.LoginScreen
import com.example.statspos.presentation.ui.screens.main.main.MainScreen
import com.example.statspos.presentation.ui.screens.main.main.SplashScreen
import com.example.statspos.presentation.viewmodels.main.LocalDataViewModel
import com.example.statspos.utils.DB
import com.example.statspos.utils.HP
import com.example.statspos.utils.showToast
import kotlinx.coroutines.flow.first
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
}

@Composable
fun App() {
    val context = LocalContext.current
    val backStack = rememberNavBackStack(Screens.Splash)
    val viewModel = hiltViewModel<LocalDataViewModel>()

    fun navigate(key: NavKey) {
        if (backStack.lastOrNull() != key) {
            backStack.add(key)
            backStack.removeFirstOrNull()
        }
    }

    LaunchedEffect(Unit) {
//        return@LaunchedEffect
        if (DB.IS_ONLINE_MODE) {
            val clientId = viewModel.getClientId().first()

            if (clientId != 0) {
                viewModel.getLoginInfo { remember, username, password ->
                    HP.clientId = clientId
                    navigate(Screens.Login(remember, username, password))
                }
            } else {
                navigate(Screens.ClientLogin)
            }
        } else {
            val localClientId = viewModel.getLocalClientId().first()
            val baseUrl = viewModel.getBaseUrl().first()

            if (localClientId != 0 && baseUrl != null) {
                DB.setBaseUrl(baseUrl)

                viewModel.getLoginInfo { remember, username, password ->
                    HP.clientId = 1
                    navigate(Screens.Login(remember, username, password))
                }
            } else {
                navigate(Screens.ClientLogin)
            }
        }
    }

    val activity = LocalActivity.current as Activity
    BackHandler {
        if (backStack.size == 1) {
            activity.finish()
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
                    onClientLogin = { clientId ->
                        viewModel.setClientId(clientId)

                        HP.clientId = clientId
                        navigate(Screens.Login(false, "", ""))
                    },
                    onLocalClientLogin = { localBranch ->
                        if (localBranch.localClientId != 0 && localBranch.baseUrl.isNotEmpty()) {
                            viewModel.setLocalClientId(localBranch.localClientId)
                            viewModel.setBaseUrl(localBranch.baseUrl)

                            context.showToast("Open App again to continue")
                            backStack.removeLastOrNull()
                        }
                    },
                    onSignup = {
                        backStack.add(Screens.ClientSignup)
//                        backStack.removeFirstOrNull()
                    }
                )
            }
            entry<Screens.ClientSignup> {
                ClientSignupScreen(
                    onSignup = { clientId ->
                        viewModel.setClientId(clientId)

                        HP.clientId = clientId
                        navigate(Screens.Login(false, "", ""))
                    }
                )
            }
            entry<Screens.Login> { key ->
                LoginScreen(
                    remember = key.remember,
                    username = key.username,
                    password = key.password,
                    onLogin = { remember, username, password ->
                        if (remember)
                            viewModel.saveLoginInfo(true, username, password)
                        else
                            viewModel.saveLoginInfo(false, "", "")

                        navigate(Screens.Main)
                    }
                )
            }
            entry<Screens.Main> {
                MainScreen()
            }
        }
    )

}
