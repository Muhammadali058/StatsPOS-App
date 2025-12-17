package com.example.statspos.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.statspos.presentation.ui.theme.StatsPOSTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StatsPOSTheme {
                Scaffold { innerPadding ->
                    App(
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}



//    val viewModel = hiltViewModel<CategoriesViewModel>()
//    val state by viewModel.state.collectAsStateWithLifecycle()
//    var showInfoDialog by remember { mutableStateOf(false) }
//    LaunchedEffect(state.infoMessage, state.error) {
//        if(state.infoMessage != null || state.error != null)
//            showInfoDialog = true
//    }

//    if (state.isLoading) {
//        Box(
//            modifier = modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            CircularProgressIndicator()
//        }
//    } else if (state.error != null) {
//        if(showInfoDialog){
//            InfoDialog(
//                title = "Error",
//                text = state.error!!
//            ) {
//                showInfoDialog = false
//            }
//        }
//    } else if (state.infoMessage != null) {
//        if(showInfoDialog){
//            InfoDialog(
//                title = "Info",
//                text = state.infoMessage!!
//            ) {
//                showInfoDialog = false
//            }
//        }
//    } else if (state.categories.isNotEmpty()) {
//        LazyColumn(
//            modifier = modifier
//        ) {
//            items(state.categories.size) { i ->
//                val category = state.categories[i]
//                Text(text = category.categoryName)
//            }
//        }
//    } else {
//        SplashScreen()
//    }
