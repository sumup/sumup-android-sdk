package com.sumup.app.util

import android.util.Log
import kotlinx.coroutines.CancellationException

internal suspend inline fun <T> safelySuspend(
    tag: String,
    operation: String,
    crossinline block: suspend () -> T,
): Result<T> = runCatching { block() }
    .onSuccess { Log.d(tag, "$operation succeeded") }
    .onFailure { error ->
        if (error is CancellationException) throw error
        Log.w(tag, "$operation failed", error)
    }
