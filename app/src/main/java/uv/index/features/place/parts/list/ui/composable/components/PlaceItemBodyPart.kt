package uv.index.features.place.parts.list.ui.composable.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.model.LatLng
import uv.index.R
import uv.index.features.place.common.getLongName
import uv.index.features.place.common.getName
import uv.index.features.place.common.latToString
import uv.index.features.place.common.lngToString
import uv.index.ui.theme.Dimens
import java.time.Instant
import java.time.ZoneId

@Composable
internal fun BodyPart(
    title: String?,
    subTitle: String?,
    latLng: LatLng,
    zoneId: ZoneId
) {
    Column(
        Modifier.padding(
            start = Dimens.grid_1,
            end = Dimens.grid_1_5
        )
    ) {
        LocationPart(latLng = latLng)
        TitlePart(
            title = title ?: "",
            subTitle = subTitle ?: ""
        )
        TimeZonePart(zoneId = zoneId)
    }
}

@Composable
private fun LocationPart(
    modifier: Modifier = Modifier,
    latLng: LatLng
) {
    Row(modifier) {
        Text(
            style = MaterialTheme.typography.labelMedium,
            text = latToString(
                latLng.latitude,
                stringResource(R.string.place_location_north),
                stringResource(R.string.place_location_south)
            )
        )
        Text(
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = Dimens.grid_1),
            text = lngToString(
                latLng.longitude,
                stringResource(R.string.place_location_east),
                stringResource(R.string.place_location_west)
            )
        )
    }
}

@Composable
private fun TitlePart(
    modifier: Modifier = Modifier,
    title: String?,
    subTitle: String?
) {
    if (title.isNullOrBlank() && subTitle.isNullOrBlank()) return
    Column(modifier) {
        if (!title.isNullOrBlank() || !subTitle.isNullOrBlank())
            Text(
                modifier = Modifier
//                    .firstBaselineToTop(32.dp)
//                    .alpha(ContentAlpha.high)
                ,
                style = MaterialTheme.typography.titleLarge,
                text = title ?: subTitle!!
            )
        if (!subTitle.isNullOrBlank() && !title.isNullOrBlank())
//            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                style = MaterialTheme.typography.bodyLarge,
                text = subTitle
            )
//            }
    }
}

@Composable
private fun TimeZonePart(
    modifier: Modifier = Modifier,
    zoneId: ZoneId
) {
    val longName = zoneId.getLongName(Instant.now()).ifEmpty { zoneId.getName() }
    Column(modifier) {
//        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            style = MaterialTheme.typography.bodyLarge,
            text = longName
        )
//        }
    }
}