package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import uv.index.R
import uv.index.common.MoreText
import uv.index.features.place.common.latToString
import uv.index.features.place.common.lngToString
import uv.index.features.place.data.room.PlaceData
import uv.index.ui.theme.Dimens

@Composable
internal fun MainPlacePart(
    modifier: Modifier = Modifier,
    place: PlaceData?,
    onEditPlace: () -> Unit
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End
    ) {

        TextButton(
            colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current),
            onClick = onEditPlace,
            contentPadding = PaddingValues(
                start = Dimens.grid_2,
                top = ButtonDefaults.TextButtonContentPadding.calculateTopPadding(),
                bottom = ButtonDefaults.TextButtonContentPadding.calculateBottomPadding(),
                end = Dimens.grid_1
            )
        ) {

            ConstraintLayout {
                val (title, icon) = createRefs()

                Column(
                    modifier = Modifier.constrainAs(title) {
                        top.linkTo(parent.top)
                        linkTo(
                            top = parent.top,
                            start = parent.start,
                            end = icon.start,
                            bottom = parent.bottom
                        )
                        width = Dimension.preferredWrapContent
                    },
                    horizontalAlignment = Alignment.End
                ) {

                    if (place?.name.isNullOrEmpty() && place?.subName.isNullOrEmpty() && place?.latLng != null) {
                        Row {
                            Text(
                                style = MaterialTheme.typography.labelMedium,
                                text = latToString(
                                    place.latLng.latitude,
                                    stringResource(R.string.place_location_north),
                                    stringResource(R.string.place_location_south)
                                )
                            )
                            Text(
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(start = Dimens.grid_1),
                                text = lngToString(
                                    place.latLng.longitude,
                                    stringResource(R.string.place_location_east),
                                    stringResource(R.string.place_location_west)
                                )
                            )
                        }

                    } else {
                        MoreText(
                            text = place?.name ?: "",
                            textStyle = MaterialTheme.typography.labelMedium,
                        )
                        if (place?.subName != null) {
                            MoreText(
                                text = place.subName,
                                textStyle = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }

                Icon(
                    modifier = Modifier
                        .padding(start = Dimens.grid_1)
                        .size(32.dp)
                        .constrainAs(icon) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        },
                    imageVector = Icons.Default.LocationOn, contentDescription = "change place",
                )


            }

//            Row(
//                modifier = Modifier,
////                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.End
//            ) {
//
//                Column(
//                    modifier = Modifier,
//                    horizontalAlignment = Alignment.End
//                ) {
//                    if (place?.name != null) {
//                        Text(
//                            modifier = Modifier,
//                            maxLines = 1,
//                            softWrap = false,
//                            text = place.name,
//                            style = MaterialTheme.typography.labelLarge,
//                        )
//                    }
//                    if (place?.subName != null) {
//                        Text(
//                            modifier = Modifier,
//                            maxLines = 1,
//                            text = place.subName,
//                            style = MaterialTheme.typography.labelLarge,
//                        )
//                    }
//                }
//                Box(
//                    modifier = Modifier
//                        .size(48.dp)
//                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        modifier = Modifier.size(32.dp),
//                        imageVector = Icons.Default.LocationOn, contentDescription = "change place",
//                    )
//                }
//            }
        }
    }
}