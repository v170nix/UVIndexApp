package uv.index.common

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.ParagraphIntrinsics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit

// https://stackoverflow.com/questions/63971569/androidautosizetexttype-in-jetpack-compose
@Suppress("LongParameterList", "MagicNumber")
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

@Composable
@Suppress("LongParameterList", "MagicNumber")
fun MoreText(
    text: String,
    modifier: Modifier = Modifier,
    literalMore: String = "...",
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

        var shrunkText by remember(Unit) {
            mutableStateOf(text)
        }

        val calculateIntrinsics = @Composable {
            ParagraphIntrinsics(
                text = shrunkText,
                style = TextStyle(
                    color = color,
                    fontSize = fontSize,
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
                var list = shrunkText.split(" ")
                if (list.size == 1 || (list.size == 2 && list.last() == literalMore)) break
                shrunkText = buildString {
                    if (list.last() == literalMore) {
                        list = list.take(list.size - 2)
                    }
                    append(list.joinToString(" "))
                    append(" ")
                    append(literalMore)
                }
                intrinsics = calculateIntrinsics()
            }
        }

        Text(
            text = shrunkText,
            modifier = modifier,
            color = color,
            maxLines = 1,
            fontSize = fontSize,
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