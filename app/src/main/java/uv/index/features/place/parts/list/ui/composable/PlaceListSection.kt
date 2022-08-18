package uv.index.features.place.parts.list.ui.composable

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import uv.index.R
import uv.index.features.place.parts.list.ui.PlaceListContract
import uv.index.features.place.parts.list.ui.PlaceListViewModel
import uv.index.features.place.parts.list.ui.composable.components.PlaceListComponent
import uv.index.ui.theme.Dimens

@Composable
internal fun PlaceListSection(
    modifier: Modifier = Modifier,
    model: PlaceListViewModel,
//    scaffoldState: ScaffoldState
) {
    val state by model.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val navigationBars = WindowInsets.navigationBars.asPaddingValues()
    val dimens = Dimens
    val density = LocalDensity.current


    val listPaddingValues by remember(navigationBars, dimens) {
        derivedStateOf {
            PaddingValues(
                start = dimens.grid_2,
                top = dimens.grid_1,
                end = dimens.grid_2,
                bottom = dimens.grid_2 + 96.0.dp +
                        navigationBars.calculateBottomPadding() -
                        navigationBars.calculateTopPadding()
            )
        }

    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {

        val undoMessage = stringResource(R.string.place_location_undo_delete_message)
        val undoString = stringResource(R.string.place_location_undo_delete_button).uppercase()

        PlaceListComponent(
            Modifier.fillMaxSize(),
            contentPadding = listPaddingValues,
            state = state,
            eventHandler = model,
            onLocationPermission = { isGrained ->
                model.doEvent(PlaceListContract.Event.UpdateLocationPermission(isGrained))
            },
            onLocationUpdate = {
                model.doEvent(PlaceListContract.Event.UpdateLocation)
            },
            onShowUndoSnackbar = { item ->
                coroutineScope.launch {
//                    val snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
//                        message = undoMessage,
//                        actionLabel = undoString
//                    )
//                    when (snackbarResult) {
//                        SnackbarResult.Dismissed -> {
//                        }
//                        SnackbarResult.ActionPerformed -> {
//                            model.doEvent(PlaceListContract.Event.UndoDeleteItem(item))
//                        }
//                    }
                }
            }
        )
    }
}