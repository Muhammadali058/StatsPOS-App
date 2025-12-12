package com.example.statspos.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.statspos.R
import com.example.statspos.presentation.ui.screens.main.ClientLoginScreen
import com.example.statspos.presentation.ui.theme.StatsPOSTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

private sealed class Screens : NavKey {
    @Serializable
    data object ClientLogin : Screens()

    @Serializable
    data class Login(val clientId: Int) : Screens()
}

@Preview(showBackground = true)
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val backStack = rememberNavBackStack(Screens.ClientLogin)
//    val viewModel = hiltViewModel<LocalDataViewModel>()

    var showSplash by remember { mutableStateOf(true) }

//    val clientId1 by viewModel.getClientId().collectAsStateWithLifecycle(0)
//    LaunchedEffect (true) {
//        viewModel.getClientId().collect { clientId ->
//            if(clientId != 0) {
//                backStack.add(Screens.Login(clientId))
//                backStack.remove(Screens.ClientLogin)
//            }
//
//            showSplash = false
//        }
//    }

    if(showSplash) {
        Box(
            modifier = modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(R.drawable.statspos_circle),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                ,
            )

        }
    }
    else{
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
                        entry<Screens.ClientLogin> {
                            ClientLoginScreen(
                                modifier = Modifier
                                    .padding(innerPadding),
                                onLogin = { clientId ->
                                    scope.launch {
//                                        viewModel.setClientId(clientId)
                                        backStack.add(Screens.Login(clientId))
                                        backStack.remove(Screens.ClientLogin)
                                    }
                                }
                            )
                        }
                        entry<Screens.Login> { key ->
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Login Screen ${key.clientId}")
                            }
                        }
                    }
                )
            }
        }
    }
}