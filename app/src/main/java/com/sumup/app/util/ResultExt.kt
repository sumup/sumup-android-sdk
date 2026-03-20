package com.sumup.app.util

import kotlinx.coroutines.CancellationException

internal fun <T> Result<T>.handleFailure(
    fallbackMessage: String,
    onError: (String) -> Unit,
): Result<T> = onFailure { error ->
    if (error is CancellationException) throw error
    val message = error.message?.takeUnless { it.isBlank() } ?: fallbackMessage
    onError(message)
}
