package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import uv.index.ui.theme.Dimens

@Composable
internal fun MainSunscreenReminder(
    modifier: Modifier = Modifier
) {

    val (checked, onChange) = remember {
        mutableStateOf(false)
    }

    Card(
        modifier = modifier
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable(
                    onClick = {
                        onChange(!checked)
                    },
                    role = Role.Switch
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = Dimens.grid_1_5),
                text = "Sunscreen reminder"
            )
            Switch(
                modifier = Modifier.padding(end = Dimens.grid_1_5),
                checked = checked,
                onCheckedChange = null
            )
        }
    }
}