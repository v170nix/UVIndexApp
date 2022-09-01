package uv.index.features.more.parts.ui

import androidx.annotation.ArrayRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringArrayResource
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.material3.Material3RichText
import uv.index.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MorePartScreen(
    @ArrayRes infoId: Int,
    onNavigateUp: () -> Unit
) {

    val scrollBehavior: TopAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(
            rememberTopAppBarState()
        )

    val info = stringArrayResource(infoId)

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = info[0]
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(vertical = Dimens.grid_1)
        ) {

            item {
                val typography = MaterialTheme.typography
                Material3RichText(
                    modifier = Modifier.padding(horizontal = Dimens.grid_2),
                    style = RichTextStyle(
                        headingStyle = { level, _ ->
                            when (level) {
                                0 -> typography.displayLarge
                                1 -> typography.displayMedium
                                2 -> typography.displaySmall
                                3 -> typography.headlineLarge
                                4 -> typography.headlineMedium
                                5 -> typography.headlineSmall
                                else -> typography.titleLarge
                            }
                        }
                    )
                ) {
                    Markdown(info[1])
                }
            }
        }
    }
}