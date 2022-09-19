package uv.index.common

import androidx.compose.runtime.Immutable

@Immutable
data class InputTextFieldState(
    val value: String,
    val onValueChange: (String) -> Unit,
    val isError: Boolean = false,
    val textError: String)