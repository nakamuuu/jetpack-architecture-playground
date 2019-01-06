package net.divlight.archplayground.extension

import android.view.View
import androidx.databinding.BindingAdapter

//
// View
//

@BindingAdapter("onUnfocused")
fun View.setOnUnfocusedListener(listener: OnUnfocusedListener) {
    setOnFocusChangeListener { _, hasFocus ->
        if (!hasFocus) listener.onUnfocused()
    }
}

interface OnUnfocusedListener {
    fun onUnfocused()
}
