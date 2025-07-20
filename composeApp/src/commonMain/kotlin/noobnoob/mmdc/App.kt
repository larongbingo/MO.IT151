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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import noobnoob.mmdc.ui.screens.LandingScreen
import noobnoob.mmdc.ui.screens.LoginScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class MoitScreen(val title: String) {
    LandingScreen("Welcome Screen"),
    LoginScreen("Login")
}

@Composable
fun MoitApp(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = MoitScreen.LandingScreen.name) {
        composable(route = MoitScreen.LandingScreen.name) {
            LandingScreen(navController)
        }
        composable(route = MoitScreen.LoginScreen.name) {
            LoginScreen()
        }
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
       MoitApp()
    }
}