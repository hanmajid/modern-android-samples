package com.hanmajid.android.material.listcontainertransform

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

/**
 * Sets an [ImageView]'s source by the given [drawable].
 */
@BindingAdapter("imageDrawable")
fun setImageDrawable(view: ImageView, drawable: Int?) {
    drawable?.let {
        view.setImageDrawable(
            ContextCompat.getDrawable(view.context, it)
        )
    }
}