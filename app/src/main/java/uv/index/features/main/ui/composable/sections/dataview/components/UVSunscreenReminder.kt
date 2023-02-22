package uv.index.features.main.ui.composable.sections.dataview.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.WorkInfo
import androidx.work.WorkManager
import uv.index.R
import uv.index.features.main.notification.Constants
import uv.index.features.main.notification.sunscreen.SunscreenNotificationWorker
import uv.index.ui.theme.Dimens

@Composable
internal fun UVSunscreenReminder(
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current


    val workState by WorkManager.getInstance(LocalContext.current)
        .getWorkInfosForUniqueWorkLiveData(Constants.SUNSCREEN_NOTIFICATION_WORK_NAME)
        .observeAsState()

    val checked by remember(workState) {
        derivedStateOf {
            workState?.firstOrNull()?.state == WorkInfo.State.ENQUEUED
        }
    }


    Card(
        modifier = modifier
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clickable(
                    onClick = {
                        if (checked) {
                            SunscreenNotificationWorker.removeWork(context)
                        } else {
                            SunscreenNotificationWorker.createWork(context)
                        }
                    },
                    role = Role.Switch
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimens.grid_1_5)
                    .weight(1f),
            ) {
                Text(
                    modifier = Modifier,
                    text = stringResource(id = R.string.uvindex_notification_sunscreen_reminder),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 20.sp
                    )
                )
                Spacer(modifier = Modifier.height(Dimens.grid_1))
                Text(
                    modifier = Modifier,
                    text = "Every hour",
                    color = LocalContentColor.current.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            Switch(
                modifier = Modifier.padding(end = Dimens.grid_1_5),
                checked = checked,
                onCheckedChange = null
            )
        }
    }
}