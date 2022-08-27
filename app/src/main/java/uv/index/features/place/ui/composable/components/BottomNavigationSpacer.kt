package uv.index.features.place.ui.composable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun BoxScope.BottomNavigationSpacer() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .height(
                WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.8f))
    )
}