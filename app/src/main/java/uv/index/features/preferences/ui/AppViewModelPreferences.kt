package uv.index.features.preferences.ui

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AppViewModelPreferences @Inject constructor(
    private val preferences: DataStore<Preferences>
) : ViewModel() {

    init {
        Log.e("init", "3")
    }

    fun saveLazyListStatePlace(state: LazyListState) {
        runBlocking {
            Log.e("save", state.firstVisibleItemIndex.toString())
            preferences.edit {
                it[LAZY_LIST_STATE_PLACE_INDEX_KEY] = state.firstVisibleItemIndex
                it[LAZY_LIST_STATE_PLACE_SCROLL_OFFSET_KEY] = state.firstVisibleItemScrollOffset
            }
        }
    }

    fun getLazyListStatePlace(): LazyListState {
        return runBlocking {
            val preferences = preferences.data.firstOrNull()
            LazyListState(
                firstVisibleItemIndex = preferences?.get(LAZY_LIST_STATE_PLACE_INDEX_KEY) ?: 0,
                firstVisibleItemScrollOffset = preferences?.get(
                    LAZY_LIST_STATE_PLACE_SCROLL_OFFSET_KEY
                ) ?: 0
            )

        }
    }

    private companion object {
        val LAZY_LIST_STATE_PLACE_INDEX_KEY =
            intPreferencesKey(name = "app.pref.place.scroll.index")

        val LAZY_LIST_STATE_PLACE_SCROLL_OFFSET_KEY =
            intPreferencesKey(name = "app.pref.place.scroll.offset")
    }

}

@Composable
fun rememberPlaceLazyListState(
    appPreferences: AppViewModelPreferences = hiltViewModel(LocalContext.current as ComponentActivity)
): LazyListState {
    val listState = rememberSaveable(
        saver = listSaver(
            save = { listOf(0) },
            restore = { appPreferences.getLazyListStatePlace() }
        )) {
        appPreferences.getLazyListStatePlace()
    }

    DisposableEffect(Unit) {
        onDispose {
            appPreferences.saveLazyListStatePlace(listState)
        }
    }

    return listState
}