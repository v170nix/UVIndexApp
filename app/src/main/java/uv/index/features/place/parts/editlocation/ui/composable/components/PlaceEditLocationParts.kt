package uv.index.features.place.parts.editlocation.ui.composable.components

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import uv.index.R
import uv.index.common.InputTextField
import uv.index.common.TextFieldState
import uv.index.features.place.data.PlaceAutocompleteResult
import uv.index.ui.theme.Dimens

@Composable
internal fun PlaceLocationSearch(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        modifier = modifier.semantics { role = Role.Button },
        onClick = {
            onClick()
        }) {
        Icon(Icons.Filled.Search, contentDescription = "Search")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun InputTitlePart(
    title: String,
    isExpanded: Boolean = false,
    onSearchClick: () -> Unit,
    onSearchResult: (PlaceAutocompleteResult) -> Unit,
    onExpandClick: () -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val placeFields = listOf(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.LAT_LNG,
        Place.Field.ADDRESS_COMPONENTS,
        Place.Field.ADDRESS
    )

    val requestAutocompleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { activityResult ->
            keyboardController?.hide()
            focusManager.clearFocus()
            val data = activityResult.data ?: return@rememberLauncherForActivityResult
            val result = when (activityResult.resultCode) {
                Activity.RESULT_OK -> PlaceAutocompleteResult.Ok(
                    Autocomplete.getPlaceFromIntent(data)
                )
                AutocompleteActivity.RESULT_ERROR -> PlaceAutocompleteResult.Error(
                    Autocomplete.getStatusFromIntent(data)
                )
                Activity.RESULT_CANCELED -> PlaceAutocompleteResult.Canceled
                else -> return@rememberLauncherForActivityResult
            }
            onSearchResult(result)
        }
    )

    val context = LocalContext.current

    val onPlaceSearchClick by rememberUpdatedState {
        if (Places.isInitialized()) {
            requestAutocompleteLauncher.launch(
                Autocomplete
                    .IntentBuilder(AutocompleteActivityMode.OVERLAY, placeFields)
                    .build(context)
            )
            onSearchClick()
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaceLocationSearch(onClick = onPlaceSearchClick)
        Text(
            modifier = Modifier
                .semantics { role = Role.Button }
                .weight(1f)
                .clickable(onClick = onPlaceSearchClick)
                .padding(horizontal = Dimens.grid_1),
            maxLines = 1,
            style = MaterialTheme.typography.titleLarge,
            text = title
        )
        IconButton(
            modifier = Modifier.semantics { role = Role.Button },
            onClick = onExpandClick
        ) {
            if (isExpanded) {
                Icon(Icons.Filled.KeyboardArrowUp, null)
            } else {
                Icon(Icons.Filled.KeyboardArrowDown, null)
            }
        }
    }
}

@Composable
internal fun InputLocationBoxPart(
    modifier: Modifier = Modifier,
    title: String?,
    subTitle: String?,
    latitudeFieldState: TextFieldState,
    longitudeFieldState: TextFieldState,
    onSearchClick: () -> Unit,
    onSearchResult: (PlaceAutocompleteResult) -> Unit,
) {
    var expandableInput by remember(Unit) { mutableStateOf(false) }
    var isShowCard by remember(Unit) { mutableStateOf(true) }
    DisposableEffect(Unit) {
        onDispose {
            isShowCard = true
        }
    }

    BackHandler(expandableInput) {
        expandableInput = false
    }

    // https://developer.android.com/codelabs/jetpack-compose-animation?hl=en&continue=https%3A%2F%2Fcodelabs.developers.google.com%2F%3Fcat%3Dandroid#3

    Card(modifier.alpha(if (isShowCard) .9f else 0f)) {
        Column(modifier = Modifier.padding(Dimens.grid_1)) {
            InputTitlePart(
                title = title ?: subTitle ?: "",
                isExpanded = expandableInput,
                onSearchClick = { isShowCard = false; onSearchClick(); },
                onSearchResult = { isShowCard = true; onSearchResult(it) },
                onExpandClick = { expandableInput = !expandableInput }
            )
            AnimatedVisibility(visible = expandableInput) {
                InputTextFieldsPart(
                    latitudeFieldState,
                    longitudeFieldState
                ) {
                    expandableInput = false
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun InputTextFieldsPart(
    latitudeFieldState: TextFieldState,
    longitudeFieldState: TextFieldState,
    onDoneClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val latitudeFocusRequester = remember { FocusRequester() }
    val longitudeFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        latitudeFocusRequester.requestFocus()
    }

    Column(Modifier.padding(Dimens.grid_1)) {
        InputTextField(
            modifier = Modifier
                .focusRequester(latitudeFocusRequester)
                .focusProperties { next },
            labelId = R.string.place_location_latitude,
            textFieldState = latitudeFieldState,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions {
                longitudeFocusRequester.requestFocus()
            },
        )
        Spacer(Modifier.height(Dimens.grid_1))

        InputTextField(
            modifier = Modifier
                .focusRequester(longitudeFocusRequester)
                .focusProperties { down },
            labelId = R.string.place_location_longitude,
            textFieldState = longitudeFieldState,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onDoneClick()
                }
            )
        )
    }
}