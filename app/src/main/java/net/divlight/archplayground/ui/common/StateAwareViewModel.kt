package net.divlight.archplayground.ui.common

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

fun <T : StateAwareViewModel> FragmentActivity.getStateAwareViewModel(
    modelClass: Class<T>,
    savedInstanceState: Bundle?
) = getStateAwareViewModel(modelClass, StateAwareViewModel.Factory(application, savedInstanceState))

fun <T : StateAwareViewModel> FragmentActivity.getStateAwareViewModel(
    modelClass: Class<T>,
    factory: StateAwareViewModel.Factory
) = ViewModelProviders.of(this, factory).get(modelClass)

fun <T : StateAwareViewModel> Fragment.getStateAwareViewModel(
    modelClass: Class<T>,
    savedInstanceState: Bundle?
) = getStateAwareViewModel(modelClass, StateAwareViewModel.Factory(requireActivity().application, savedInstanceState))

fun <T : StateAwareViewModel> Fragment.getStateAwareViewModel(
    modelClass: Class<T>,
    factory: StateAwareViewModel.Factory
) = ViewModelProviders.of(this, factory).get(modelClass)

/**
 * Factoryからの生成時にsavedInstanceStateからの復元を強制するViewModel
 */
abstract class StateAwareViewModel(application: Application) : AndroidViewModel(application) {
    abstract fun saveInstanceState(outState: Bundle)
    abstract fun restoreInstanceState(state: Bundle?)

    open class Factory(
        private val application: Application,
        private val savedInstanceState: Bundle?
    ) : ViewModelProvider.NewInstanceFactory() {
        final override fun <T : ViewModel> create(modelClass: Class<T>): T = createBase(modelClass)
            .also { (it as StateAwareViewModel).restoreInstanceState(savedInstanceState) }

        open fun <T : ViewModel> createBase(modelClass: Class<T>): T = modelClass
            .getConstructor(Application::class.java)
            .newInstance(application)
    }
}
