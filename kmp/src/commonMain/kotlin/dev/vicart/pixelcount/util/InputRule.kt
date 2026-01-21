package dev.vicart.pixelcount.util

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class InputRule<T>(
    value: T,
    validator: (T) -> Boolean,
    errorMessage: String
) {
    val isValid = validator(value)

    val error = (@Composable {
        Text(errorMessage)
    }).takeIf { !isValid }
}

@Composable
fun <T> rememberInputRule(
    value: T,
    validator: (T) -> Boolean,
    errorMessage: String
) : InputRule<T> {
    return remember(value, validator, errorMessage) {
        InputRule(value, validator, errorMessage)
    }
}