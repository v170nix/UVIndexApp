package uv.index

import android.app.Dialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import dagger.hilt.android.AndroidEntryPoint
import uv.index.features.preferences.ui.ThemeViewModel
import uv.index.navigation.AppScreen
import uv.index.ui.AppNavGraph
import uv.index.ui.theme.UVIndexAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var errorDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        checkGooglePlayServices()
        setContent {
            val themeViewModel = hiltViewModel<ThemeViewModel>(this)
            val themeState by themeViewModel.state.collectAsState()

            UVIndexAppTheme(
                themeMode = themeState
            ) {
                CompositionLocalProvider(LocalAppState provides rememberAppState()) {
                    AppNavGraph(startDestination = AppScreen.Main.route)
                }
            }
        }
    }

    private fun checkGooglePlayServices(): Boolean {
        val googleApiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode: Int = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                if (errorDialog == null) {
                    errorDialog = googleApiAvailability.getErrorDialog(this, resultCode, 2404)
                    errorDialog?.setCancelable(false)
                }
                if (errorDialog?.isShowing == false) errorDialog?.show()
            }
        }
        return resultCode == ConnectionResult.SUCCESS
    }

}
