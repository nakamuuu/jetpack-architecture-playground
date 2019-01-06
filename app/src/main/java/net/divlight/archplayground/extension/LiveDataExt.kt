package net.divlight.archplayground.extension

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observe(owner: LifecycleOwner, observer: (T?) -> Unit) {
    observe(owner, Observer { observer(it) })
}

fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, observer: (T) -> Unit) {
    observe(owner, Observer { value -> value?.let { observer(it) } })
}

fun <T> LiveData<T>.observeNonNullForever(observer: (T) -> Unit) {
    observeForever { value -> value?.let { observer(it) } }
}
