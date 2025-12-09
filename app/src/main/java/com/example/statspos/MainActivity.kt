package com.example.statspos

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.statspos.components.OutlinedTextbox
import com.example.statspos.components.PasswordOutlinedTextbox
import com.example.statspos.ui.theme.StatsPOSTheme
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Main()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Main() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val dataStore = remember {
        LocalData(context)
    }

//    val clientId by dataStore.clientId.collectAsStateWithLifecycle(0)

    LaunchedEffect(true) {
        Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()
//        dataStore.clientId.collect{
//            Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
//        }
//        clientId?.run {
//            if(clientId != 0)
//                Toast.makeText(context, clientId.toString(), Toast.LENGTH_SHORT).show()
//            else
//                Toast.makeText(context, "Already Set", Toast.LENGTH_SHORT).show()
//        }
    }

    StatsPOSTheme {
        Scaffold() { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    Modifier.height(32.dp)
                )
                Image(
                    painterResource(R.drawable.statspos),
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                )
                Spacer(
                    Modifier.height(16.dp)
                )
                Text(
                    text = "Welcome Back!",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = "Let's Start Today's Business",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSecondary,
                        fontSize = 18.sp,
                    )
                )
                Spacer(
                    Modifier.height(16.dp)
                )
                OutlinedTextbox(
                    value = username,
                    onValueChange = { username = it },
                    labelText = "Username",
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = {
                        Icon(painterResource(R.drawable.ic_user), null)
                    }
                )
                PasswordOutlinedTextbox(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = {
                        Icon(painterResource(R.drawable.ic_password), null)
                    },
                    onKeyboardActionsDone = {
                        Toast.makeText(context, "Login", Toast.LENGTH_SHORT).show()
                    }
                )
                Spacer(
                    Modifier.height(16.dp)
                )
                Button(
                    onClick = {
                        scope.launch {
                            dataStore.setClientId(1)
//                            login(context, username, password)
                        }
                    },
                    modifier = Modifier
                        .width(120.dp)
                ) {
                    Text("Login")
                }
            }
        }
    }
}

private fun login(context: Context, username:String, password:String) {
    Toast.makeText(context, username, Toast.LENGTH_SHORT).show()
}

class LocalData(private val context: Context){
    val Context.dataStore by preferencesDataStore("clientInfo")

    val clientId = context.dataStore.data.map { preferences -> preferences[clientIdKey] ?: 0 }

    suspend fun setClientId(clientId:Int){
        context.dataStore.edit { settings ->
            settings[clientIdKey] = clientId
        }
    }

    companion object {
        val clientIdKey = intPreferencesKey("clientId")
    }
}