package noobnoob.mmdc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun MainApp(navHostController: NavHostController) {
    Column(modifier = Modifier.safeContentPadding()) {
        Text("LOGGED IN. IMPLEMENT API FETCHES")
    }
}