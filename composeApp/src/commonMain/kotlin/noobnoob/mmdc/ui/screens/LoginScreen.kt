package noobnoob.mmdc.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun LoginScreen() {
    Column() {
        Spacer(Modifier.weight(1f))
        Text("Login Screen")
        Spacer(Modifier.weight(1f))
    }
}