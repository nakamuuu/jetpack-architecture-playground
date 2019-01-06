package net.divlight.archplayground.extension

import android.content.Context
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel

val AndroidViewModel.context: Context get() = getApplication()
val AndroidViewModel.resources: Resources get() = context.resources
fun AndroidViewModel.getString(@StringRes resId: Int) = context.getString(resId)
fun AndroidViewModel.getString(@StringRes resId: Int, vararg formatArgs: Any) = context.getString(resId, *formatArgs)
