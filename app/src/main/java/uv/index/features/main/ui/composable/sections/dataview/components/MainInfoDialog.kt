package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
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
    val data = state.data

    MainInfoDialog(
        headline = data.headline,
        onDismissRequest = {
            state.openDialog = false
        }
    ) {
        if (data.info != null) {
            item {
                Text(
                    text = data.info,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun MainInfoDialog(
    headline: String,
//    usePlatformDefaultWidth: Boolean = true,
//    isViewCloseButton: Boolean = false,
    onDismissRequest: () -> Unit,
    info: (LazyListScope.() -> Unit)?,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
            securePolicy = SecureFlagPolicy.SecureOff
        )
    ) {
        Card(
            modifier = Modifier.padding(Dimens.grid_2),
            shape = MaterialTheme.shapes.extraLarge
        ) {

            Column {
                Row(modifier = Modifier.padding(top = 8.dp, start = 24.dp, end = 8.dp)) {
                    Text(
                        modifier = Modifier.weight(1f)
//                                    .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = Dimens.grid_1),
                        text = headline,
                        style = MaterialTheme.typography.headlineSmall,
                    )
//                    if (isViewCloseButton) {
                        IconButton(
                            onClick = { onDismissRequest() },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "close",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
//                    }
                }
                LazyColumn(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(Dimens.grid_2)
                ) {
                    if (info != null) {
                        info()
                    }
                }
            }
        }
    }
}