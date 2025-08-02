package noobnoob.mmdc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import noobnoob.mmdc.api.MoitWebApi
import noobnoob.mmdc.oauth.pkce.UserSession
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi

private val webApi = MoitWebApi(UserSession.sessionToken!!)

@OptIn(ExperimentalUuidApi::class)
@Composable
@Preview
fun MainApp(navHostController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }
    var externalId by remember { mutableStateOf("") }

    Column(modifier = Modifier.safeContentPadding()) {
        Text("LOGGED IN. IMPLEMENT API FETCHES")
        Text(UserSession.sessionToken!!)
        Text(username)
        Text(id)
        Text(externalId)
    }

    LaunchedEffect("MainApp") {
        val user = webApi.getUser()
        if (user != null) {
            username = user.username
            id = user.id.toString()
            externalId = user.externalId
        }
    }
}