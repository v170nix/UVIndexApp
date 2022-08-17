package uv.index.parts.main.ui.composable.sections.emptyplace

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import uv.index.R

@Composable
internal fun EmptyPlaceSection(
    modifier: Modifier = Modifier,
    onAddPlaceScreen: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .width(148.dp)
                .height(148.dp),
            painter = painterResource(id = R.drawable.ic_empty_place),
//            tint = MaterialTheme.colors.secondary,
            contentDescription = null
        )
//        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
                text = stringResource(id = R.string.place_empty_title),
//                style = MaterialTheme.typography.h5
            )
//        }
//        Spacer(modifier = androidx.compose.ui.Modifier.height(Dimens.grid_2))
//        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(id = R.string.place_empty_text),
//                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center
            )
//        }
//        Spacer(modifier = androidx.compose.ui.Modifier.height(Dimens.grid_2))
        Button(onClick = onAddPlaceScreen) {
            Text(
                text = stringResource(id = R.string.place_empty_button)
                    .toUpperCase(Locale.current),
//                style = MaterialTheme.typography.button
            )
        }
    }
}
