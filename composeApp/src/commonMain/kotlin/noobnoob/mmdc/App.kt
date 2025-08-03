package noobnoob.mmdc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import noobnoob.mmdc.ui.screens.FirstTimeLoginScreen
import noobnoob.mmdc.ui.screens.LandingScreen
import noobnoob.mmdc.ui.screens.LoginScreen
import noobnoob.mmdc.ui.screens.MainApp
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class MoitScreen() {
    LandingScreen,
    LoginScreen,
    FirstTimeLogin,
    App
}

@Composable
@Preview
fun App(navController: NavHostController = rememberNavController(),
    onNavHostReady: suspend (NavController) -> Unit = {}) {
    MaterialTheme {
        NavHost(navController, startDestination = MoitScreen.LandingScreen.name) {
            composable(route = MoitScreen.LandingScreen.name) {
                LandingScreen(navController)
            }
            composable(route = MoitScreen.LoginScreen.name) {
                LoginScreen(navController)
            }
            composable(route = MoitScreen.FirstTimeLogin.name) {
                FirstTimeLoginScreen(navController)
            }
            composable(route = MoitScreen.App.name) {
                MainApp(navController)
            }
        }
        LaunchedEffect(navController) {
            onNavHostReady(navController)
        }
    }
}