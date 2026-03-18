package com.example.statspos.presentation

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.domain.models.main.Clients
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
}

@Composable
fun App() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backStack = rememberNavBackStack(Screens.Splash)
    val viewModel = hiltViewModel<LocalDataViewModel>()

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
            }
        }

        navigate(Screens.Login(false, "", ""))
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
                    onClientLogin = { client ->
                        showLoginScreen(client)
                    },
                    onSignup = {
                        backStack.add(Screens.ClientSignup)
//                        backStack.removeFirstOrNull()
                    }
                )
            }
            entry<Screens.ClientSignup> {
                ClientSignupScreen(
                    onSignup = { client ->
                        showLoginScreen(client)
                    }
                )
            }
            entry<Screens.Login> { key ->
                LoginScreen(
                    remember = key.remember,
                    username = key.username,
                    password = key.password,
                    onLogin = {
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
