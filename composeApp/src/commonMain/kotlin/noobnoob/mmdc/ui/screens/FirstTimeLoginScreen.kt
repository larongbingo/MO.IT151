package noobnoob.mmdc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import noobnoob.mmdc.MoitScreen
import noobnoob.mmdc.api.MoitHttpClient
import noobnoob.mmdc.api.MoitWebApi
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun FirstTimeLoginScreen(
    navHostController: NavHostController = rememberNavController(),
    webApi: MoitWebApi = MoitWebApi()
) {
    var username by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "First Time Login", fontSize = 20.sp)
        Text("Enter a Username to proceed to the app")
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            maxLines = 1
        )
        if (isError) {
            Text(text = "An error occurred, try again")
        }
        Spacer(modifier = Modifier.weight(0.2f))
        Button(
            onClick = {
                scope.launch {
                    val userId = webApi.createUser(username = username)
                    if (userId != null) {
                        navHostController.navigate(MoitScreen.App.name)
                    } else {
                        isError = true
                    }
                }
            },
            enabled = username.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            content = { Text("Enter") }
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}
