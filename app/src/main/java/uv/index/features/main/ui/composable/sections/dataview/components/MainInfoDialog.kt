package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.SecureFlagPolicy
import uv.index.ui.theme.Dimens

@Stable
data class MainInfoData(
    val headline: String,
    val data: String? = null,
    val info: String? = null,
)

@Stable
class MainInfoState(data: MainInfoData) {
    var openDialog by mutableStateOf(false)
    var data by mutableStateOf(data)
}

@Composable
fun MainInfoHost(
    state: MainInfoState,
    info: @Composable (MainInfoState) -> Unit = { MainInfoDialogPart(it) }
) {
    if (state.openDialog) {
        info(state)
    }
}


@Composable
private fun MainInfoDialogPart(
    state: MainInfoState
) {
    Dialog(
        onDismissRequest = { state.openDialog = false },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            securePolicy = SecureFlagPolicy.SecureOff
        )
    ) {

        val data = state.data

        Card(shape = MaterialTheme.shapes.extraLarge) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(Dimens.grid_2)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = data.headline, // "Vitamin D",
                    style = MaterialTheme.typography.headlineSmall
                )

                if (data.info != null) {
                    Text(
                        text = data.info,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}