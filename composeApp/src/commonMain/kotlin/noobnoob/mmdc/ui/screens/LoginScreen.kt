package noobnoob.mmdc.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.multiplatform.webview.request.RequestInterceptor
import com.multiplatform.webview.request.WebRequest
import com.multiplatform.webview.request.WebRequestInterceptResult
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewNavigator
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import moit151.composeapp.generated.resources.Res
import moit151.composeapp.generated.resources.material_symbols_rounded_arrow_back
import noobnoob.mmdc.MoitScreen
import noobnoob.mmdc.api.MoitWebApi
import noobnoob.mmdc.oauth.pkce.OauthPkceAuthenticationService
import noobnoob.mmdc.oauth.pkce.OauthPkceUrlBuilder
import noobnoob.mmdc.oauth.pkce.UserSession
import noobnoob.mmdc.oauth.pkce.UserSessionResponse
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val auth = OauthPkceUrlBuilder(
    domain = "ewan.au.auth0.com",
    clientId = "h5m8clc3ztoWe0brx1qHZR9FDQ7GIltL",
    redirectUri = "https://localhost:7295/swagger/oauth2-redirect.html",
    audience = "https://ewan/api",
    scope = "openid offline_access"
)

private val authService = OauthPkceAuthenticationService(auth)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun LoginScreen(navHostController: NavHostController) {
    val authState = rememberWebViewState(auth.buildAuthorizationUrl())
    val scope = rememberCoroutineScope()
    val navigationState = rememberWebViewNavigator(
        requestInterceptor = LoginRequestInterceptor(scope, navHostController)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navHostController.popBackStack() },
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.material_symbols_rounded_arrow_back),
                            contentDescription = "Navigate back"
                        )
                    }
                },
                title = {
                    Text("Login")
                }
            )
        }
    ) {
        WebView(
            state = authState,
            navigator = navigationState,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private class LoginRequestInterceptor(
    val scope: CoroutineScope,
    val navHostController: NavHostController,
) : RequestInterceptor {
    override fun onInterceptUrlRequest(request: WebRequest, navigator: WebViewNavigator) : WebRequestInterceptResult {
        if (request.url.contains(auth.redirectUri)) {
            navigator.stopLoading()

            val url = Url(request.url)
            if (url.parameters.contains("code")) {
                val authorizationCode = url.parameters["code"]!!
                scope.launch {
                    val sessionToken =
                        authService.fetchSessionTokensWithAuthorizationCode(authorizationCode)
                    if (sessionToken != null) {
                        UserSession.sessionToken = sessionToken.access_token
                        val user = MoitWebApi(sessionToken.access_token).getUser() // Goat says, this is prolly smelly since it blocks testability and tightly couples this func
                        if (user == null) {
                            navHostController.navigate(MoitScreen.FirstTimeLogin.name)
                        } else {
                            navHostController.navigate(MoitScreen.App.name)
                        }
                    } else {
                        navHostController.popBackStack() // TODO: add message on failed token grant authn flow
                    }
                }
            } else if (url.parameters.contains("error_description")) {
                navHostController.popBackStack() // TODO: pass error_description
            } else {
                navHostController.popBackStack() // TODO: add generic error
            }

            return WebRequestInterceptResult.Reject
        }

        return WebRequestInterceptResult.Allow
    }
}
