package uv.index.common

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.ParagraphIntrinsics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import net.arwix.extension.ConflatedJob

@Composable
fun LifecycleTimer(
    timeMillis: Long,
    isFirstDelay: Boolean = false,
    block: suspend () -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = rememberCoroutineScope()
    DisposableEffect(lifecycle) {
        val job = ConflatedJob()
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    job += scope.launch {
                        while (isActive) {
                            if (isFirstDelay) delay(timeMillis)
                            block()
                            if (!isFirstDelay) delay(timeMillis)
                        }
                    }
                }
                Lifecycle.Event.ON_STOP -> {
                    job.cancel()
                }
                else -> {
                }
            }
        }
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            job.cancel()
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
}


// https://stackoverflow.com/questions/63971569/androidautosizetexttype-in-jetpack-compose
@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textStyle: TextStyle = LocalTextStyle.current,
    fontSize: TextUnit = textStyle.fontSize,
    fontStyle: FontStyle? = textStyle.fontStyle,
    fontWeight: FontWeight? = textStyle.fontWeight,
    fontFamily: FontFamily? = textStyle.fontFamily,
    letterSpacing: TextUnit = textStyle.letterSpacing,
    textDecoration: TextDecoration? = textStyle.textDecoration,
    textAlign: TextAlign? = textStyle.textAlign,
    lineHeight: TextUnit = textStyle.lineHeight,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    BoxWithConstraints {
        var shrunkFontSize = fontSize

        val calculateIntrinsics = @Composable {
            ParagraphIntrinsics(
                text, TextStyle(
                    color = color,
                    fontSize = shrunkFontSize,
                    fontWeight = fontWeight,
                    textAlign = textAlign,
                    lineHeight = lineHeight,
                    fontFamily = fontFamily,
                    textDecoration = textDecoration,
                    fontStyle = fontStyle,
                    letterSpacing = letterSpacing
                ),
                density = LocalDensity.current,
                fontFamilyResolver = LocalFontFamilyResolver.current
            )
        }

        var intrinsics = calculateIntrinsics()
        with(LocalDensity.current) {
            while (intrinsics.maxIntrinsicWidth > maxWidth.toPx()) {
                shrunkFontSize *= 0.9
                intrinsics = calculateIntrinsics()
            }
        }
        Text(
            text = text,
            modifier = modifier,
            color = color,
            fontSize = shrunkFontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            onTextLayout = onTextLayout,
            style = textStyle
        )
    }
}