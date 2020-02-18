package com.longle.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.longle.data.Result
import retrofit2.Response
import timber.log.Timber

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun RecyclerView.removeAllItemDecoration() {
    while (itemDecorationCount > 0) {
        removeItemDecorationAt(0)
    }
}

suspend fun <T> (suspend () -> Response<T>).toResult(): Result<T> {
    try {
        val response = invoke()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) return Result.success(body)
        }
        return error(" ${response.code()} ${response.message()}")
    } catch (e: Exception) {
        return error(e.message ?: e.toString())
    }
}

private fun <T> error(message: String): Result<T> {
    Timber.e(message)
    return Result.error("Network call has failed for a following reason: $message")
}
