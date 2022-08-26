package uv.index.features.preference.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uv.index.navigation.AppNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreen() {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {

        },
        bottomBar = {
            AppNavigationBar()
        }
    ) {
        Column(Modifier.padding(it)) {
            Text("Preference Screen")
        }
    }
}