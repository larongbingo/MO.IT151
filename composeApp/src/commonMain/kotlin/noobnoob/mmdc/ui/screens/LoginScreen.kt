package noobnoob.mmdc.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moit151.composeapp.generated.resources.Res
import moit151.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun LoginScreen() {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(44.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FINMARK"
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(painterResource(Res.drawable.compose_multiplatform), null)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()

        ) {
            Text(text = "Login")
        }
    }


}