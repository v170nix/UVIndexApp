package uv.index.features.main.ui.composable.sections.emptyplace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import uv.index.R
import uv.index.ui.theme.Dimens

@Composable
@Suppress("MagicNumber")
internal fun EmptyPlaceSection(
    modifier: Modifier = Modifier,
    onAddPlaceScreen: () -> Unit
) {
    Surface(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier
                    .alpha(0.5f)
                    .width(148.dp)
                    .height(148.dp),
                painter = painterResource(id = R.drawable.ic_empty_place),
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = null
            )
            Text(
                text = stringResource(id = R.string.place_empty_title),
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(Dimens.grid_1))
            Text(
                modifier = Modifier.padding(horizontal = Dimens.grid_2),
                text = stringResource(id = R.string.place_empty_text),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(Dimens.grid_2))
            Button(onClick = onAddPlaceScreen) {
                Text(
                    text = stringResource(id = R.string.place_empty_button).toUpperCase(Locale.current),
                )
            }
        }
    }
}