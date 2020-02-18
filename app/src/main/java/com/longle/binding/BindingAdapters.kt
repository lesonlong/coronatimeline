package com.longle.binding

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("datetime")
fun bindDatetime(textView: TextView, time: Long) {
    textView.text = if (time == Long.MAX_VALUE) {
        "Hiện Tại"
    } else {
        SimpleDateFormat("HH:mm", Locale("vi", "VN")).format(time)
    }
}

@BindingAdapter("refreshing")
fun bindIsRefreshing(swipeRefreshLayout: SwipeRefreshLayout, refreshing: Boolean) {
    swipeRefreshLayout.isRefreshing = refreshing
}

@BindingAdapter("onRefreshListener")
fun bindOnRefreshListener(swipeRefreshLayout: SwipeRefreshLayout, onRefreshListener: () -> Unit) {
    swipeRefreshLayout.setOnRefreshListener { onRefreshListener.invoke() }
}

@BindingAdapter("imageUrl")
fun bindImageUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
            .load(imageUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view)
    }
}
