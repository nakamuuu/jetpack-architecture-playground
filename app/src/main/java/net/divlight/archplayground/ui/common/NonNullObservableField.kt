package net.divlight.archplayground.ui.common

import androidx.databinding.Observable
import androidx.databinding.ObservableField

/**
 * Nullableな値を保持させない [ObservableField]
 *
 * https://medium.com/meesho-tech/non-null-observablefield-in-kotlin-bd72d31ab54f
 */
open class NonNullObservableField<T : Any> : ObservableField<T> {
    constructor(value: T) : super(value)
    constructor(vararg dependencies: Observable) : super(*dependencies)

    override fun get(): T = super.get() ?: throw IllegalStateException("value must not be null.")

    @Suppress("RedundantOverride")
    override fun set(value: T) {
        super.set(value)
    }
}
