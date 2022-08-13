package uv.index.parts.main.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun MainProtectionPart(
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalContentColor provides contentColorFor(
            MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier,
                text = "Защита рекомендована",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                modifier = Modifier,
                text = "с 10:50 по 14:29",
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }

}