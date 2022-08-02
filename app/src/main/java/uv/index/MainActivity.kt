package uv.index

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import uv.index.navigation.AppScreen
import uv.index.ui.AppNavGraph
import uv.index.ui.theme.UVIndexAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            UVIndexAppTheme {
                CompositionLocalProvider(LocalAppState provides rememberAppState()) {
                    AppNavGraph(startDestination = AppScreen.Main.route)
                }



//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .systemBarsPadding()
//                        .navigationBarsPadding(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Greeting("Android")
//                }
//
//                val systemUiController = rememberSystemUiController()
//                SideEffect {
//                    systemUiController.setSystemBarsColor(
//                        color = Color.Transparent,
//                        darkIcons = true
//                    )
////                        systemUiController.setNavigationBarColor()
//                }
            }
        }
    }
}

//@Composable
//fun Greeting(name: String) {
//    Text(
//        modifier = Modifier,
//        text = "Hello $name!",
//        color = Color.Black
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    UVIndexAppTheme {
//        Greeting("Android")
//    }
//}