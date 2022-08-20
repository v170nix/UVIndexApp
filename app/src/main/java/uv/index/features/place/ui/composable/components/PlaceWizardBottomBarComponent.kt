package uv.index.features.place.ui.composable.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import uv.index.ui.theme.Dimens

@Composable
fun PlaceWizardBottomBarComponent(
    isEnableNextStep: Boolean,
    isShowNextStep: Boolean = true,
    previousName: String,
    nextName: String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.grid_2)
            .navigationBarsPadding()
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = Dimens.grid_2)
        ) {

            Button(
                modifier = Modifier.weight(1f).semantics { role = Role.Button },
                content = {
                    Text(
                        text = previousName.toUpperCase(Locale.current),
                    )
                },
                onClick = onPreviousClick
            )

            Spacer(modifier = Modifier.width(16.dp))

            if (isShowNextStep) {

                Row(
                    modifier = Modifier
                        .weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = Modifier.fillMaxWidth().semantics { role = Role.Button },
                        content = {
                            Text(
                                text = nextName.toUpperCase(Locale.current),
                            )
                        },
                        onClick = onNextClick,
                        enabled = isEnableNextStep
                    )
                }
            }

        }

    }

}