package uv.index.features.place.parts.editzone.ui.composable

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import net.arwix.mvi.EventHandler
import uv.index.R
import uv.index.features.place.data.gtimezone.TimeZoneDisplayEntry
import uv.index.features.place.parts.editzone.ui.PlaceEditTimeZoneContract
import uv.index.features.place.parts.editzone.ui.PlaceEditTimeZoneContract.State
import uv.index.features.place.parts.editzone.ui.PlaceEditTimeZoneContract.State.AutoTimeZoneEntry
import uv.index.features.place.parts.editzone.ui.PlaceEditTimeZoneContract.State.Companion.isSelectedItem
import uv.index.ui.theme.Dimens

@Composable
fun PlaceEditTimeZoneSection(
    modifier: Modifier = Modifier,
    state: State,
    eventHandler: EventHandler<PlaceEditTimeZoneContract.Event>,
    contentListPadding: PaddingValues = PaddingValues(0.dp),
    selectedColor: Color = MaterialTheme.colorScheme.tertiary,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    headerBackgroundColor: Color = MaterialTheme.colorScheme.background,
) {
    Column(modifier.fillMaxSize()) {
        LocationZoneList(
            modifier = Modifier.weight(1f).background(backgroundColor),
            places = state.listZones,
            autoTimeZoneEntry = state.autoTimeZoneEntry,
            selectedItem = state.selectedItem,
            onSelect = { item ->
                eventHandler.doEvent(
                    PlaceEditTimeZoneContract.Event.SelectItem(
                        item
                    )
                )
            },
            contentListPadding = contentListPadding,
            selectedColor = selectedColor,
            backgroundColor = backgroundColor,
            headerBackgroundColor = headerBackgroundColor
        )
    }
}

@Composable
private fun LocationZoneList(
    modifier: Modifier = Modifier,
    places: List<TimeZoneDisplayEntry>,
    autoTimeZoneEntry: AutoTimeZoneEntry?,
    selectedItem: State.SelectedItem?,
    onSelect: (State.SelectedItem) -> Unit,
    contentListPadding: PaddingValues,
    selectedColor: Color,
    backgroundColor: Color,
    headerBackgroundColor: Color,
) {

    val autoItemIsSelected by remember(autoTimeZoneEntry, selectedItem) {
        derivedStateOf {
            if (autoTimeZoneEntry is AutoTimeZoneEntry.Ok) {
                autoTimeZoneEntry.isSelectedItem(selectedItem)
            } else false
        }
    }

    val autoItemBgColor by animateColorAsState(
        if (autoItemIsSelected) selectedColor else backgroundColor
    )

    val autoPlaceModifier by remember(autoTimeZoneEntry, selectedItem) {
        derivedStateOf {
            if (autoTimeZoneEntry is AutoTimeZoneEntry.Ok) {
                val isSelectedItem = autoTimeZoneEntry.isSelectedItem(selectedItem)
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelectedItem,
                        onClick = {
                            onSelect(State.SelectedItem.FromAutoTimeZone(autoTimeZoneEntry))
                        },
                        role = Role.Button
                    )
                    .background(autoItemBgColor)
            } else {
                Modifier
                    .fillMaxWidth()
                    .background(autoItemBgColor)
            }
        }
    }


    LazyColumn(
        modifier = modifier,
        contentPadding = contentListPadding
    ) {

        item {
            HeaderRow(
                text = stringResource(R.string.place_location_header_auto),
                backgroundColor = headerBackgroundColor
            )
        }

        item {
            AutoDetectRow(autoPlaceModifier, autoTimeZoneEntry)
        }

        item {
            HeaderRow(
                text = stringResource(R.string.place_location_header_list),
                backgroundColor = headerBackgroundColor
            )
        }

        items(places) { item ->
            val isSelectedItem = item.isSelectedItem(selectedItem)
            val bgColor by animateColorAsState(
                if (isSelectedItem) selectedColor else backgroundColor
            )

            CustomItemRow(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = isSelectedItem,
                        onClick = { onSelect(State.SelectedItem.FromList(item)) },
                        role = Role.Button
                    )
                    .background(bgColor),
                item
            )
        }
    }

}

@Composable
private fun HeaderRow(text: String, backgroundColor: Color) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = Dimens.grid_2, vertical = Dimens.grid_1),
        text = text.toUpperCase(Locale.current),
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
private fun AutoDetectRow(
    modifier: Modifier = Modifier,
    entry: AutoTimeZoneEntry?,
) {

    Crossfade(
        modifier = modifier,
        targetState = entry,
        animationSpec = tween(75)
    ) { item ->
        Box(
            modifier = Modifier.then(
                Modifier
                    .heightIn(min = 72.dp)
                    .fillMaxWidth()
            ),
            contentAlignment = Alignment.Center
        ) {
            when (item) {
                AutoTimeZoneEntry.Denied -> NotPremiumItemRow()
                is AutoTimeZoneEntry.Error -> ErrorItemRow()
                is AutoTimeZoneEntry.Ok -> CustomItemRow(item = item.timeZoneDisplayEntry)
                else -> {
                    // loading
                    LoadingItemRow()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotPremiumItemRow() {
    ListItem(
        headlineText = {
            Column {
                Text(
                    text = stringResource(R.string.place_location_premium_info),
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErrorItemRow() {
    ListItem(
        headlineText = {
            Text(
                text = stringResource(R.string.place_location_error_time_zone_auto_find),
                color = MaterialTheme.colorScheme.error
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun LoadingItemRow() {
    CircularProgressIndicator()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomItemRow(
    modifier: Modifier = Modifier,
    item: TimeZoneDisplayEntry
) {
    ListItem(
        modifier = modifier,
        supportingText = {
            Text(
                text = item.displayLongName,
                maxLines = 2
            )
        },
        trailingContent = {
            Text(
                text = item.gmtOffsetString,
                maxLines = 1
            )
        },
        headlineText = {
            Text(
                text = item.displayName,
                maxLines = 1
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}