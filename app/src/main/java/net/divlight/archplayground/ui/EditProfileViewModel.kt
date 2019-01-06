package net.divlight.archplayground.ui

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.annotation.VisibleForTesting
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import net.divlight.archplayground.BR
import net.divlight.archplayground.R
import net.divlight.archplayground.data.Prefecture
import net.divlight.archplayground.data.Profile
import net.divlight.archplayground.data.ProfileRepository
import net.divlight.archplayground.data.ProfileValidator
import net.divlight.archplayground.extension.getString
import net.divlight.archplayground.ui.common.NonNullObservableField
import net.divlight.archplayground.ui.common.SingleLiveEvent
import net.divlight.archplayground.ui.common.StateAwareViewModel

class EditProfileViewModel @VisibleForTesting constructor(
    application: Application,
    private val repository: ProfileRepository
) : StateAwareViewModel(application), LifecycleObserver {
    companion object {
        private const val STATE_KEY_PROFILE = "profile"

        private const val INITIAL_BIRTH_YEAR = 1990
        private const val INITIAL_BIRTH_MONTH = 1
        private const val INITIAL_BIRTH_DAY = 1
    }

    @Suppress("unused")
    constructor(application: Application) : this(application, ProfileRepository.getInstance())

    val viewState = ObservableField<ViewState>()
    val profile = Profile().apply {
        addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            // 各プロパティが変化した段階でその入力欄のエラーメッセージを非表示にする
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                when (propertyId) {
                    BR.lastName -> validationErrors.set(validationErrors.get().copy(lastNameError = null))
                    BR.firstName -> validationErrors.set(validationErrors.get().copy(firstNameError = null))
                    BR.kanaLastName -> validationErrors.set(validationErrors.get().copy(kanaLastNameError = null))
                    BR.kanaFirstName -> validationErrors.set(validationErrors.get().copy(kanaFirstNameError = null))
                    BR.phoneNumber -> validationErrors.set(validationErrors.get().copy(phoneNumberError = null))
                    BR.birthday -> validationErrors.set(validationErrors.get().copy(birthdayError = null))
                    BR.zipCode -> validationErrors.set(validationErrors.get().copy(zipCodeError = null))
                    BR.prefecture -> validationErrors.set(validationErrors.get().copy(prefectureError = null))
                    BR.cities -> validationErrors.set(validationErrors.get().copy(citiesError = null))
                    BR.address1 -> validationErrors.set(validationErrors.get().copy(address1Error = null))
                    BR.address2 -> validationErrors.set(validationErrors.get().copy(address2Error = null))
                }
            }
        })
    }
    val validationErrors = NonNullObservableField(ValidationErrors())

    val showBirthdayPickerEvent = SingleLiveEvent<ShowBirthdayPickerEvent>()
    val showPrefectureSelectionEvent = SingleLiveEvent<EmptyEventObject>()
    val showToastEvent = SingleLiveEvent<ShowToastEvent>()
    val showSnackbarEvent = SingleLiveEvent<ShowSnackbarEvent>()
    val finishActivityEvent = SingleLiveEvent<EmptyEventObject>()

    private val disposables = CompositeDisposable()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        if (viewState.get() == null) {
            fetchProfile()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }

    override fun saveInstanceState(outState: Bundle) {
        outState.putSerializable(STATE_KEY_PROFILE, profile)
    }

    override fun restoreInstanceState(state: Bundle?) {
        if (state?.containsKey(STATE_KEY_PROFILE) == true) {
            viewState.set(ViewState.Fetched)
            profile.set(state.getSerializable(STATE_KEY_PROFILE) as Profile)
        }
    }

    private fun fetchProfile() {
        viewState.set(ViewState.Fetching)
        disposables.add(
            repository.fetchProfile()
                .subscribe({ profile ->
                    this.profile.set(profile)
                    viewState.set(ViewState.Fetched)
                }, {
                    showToastEvent.postValue(ShowToastEvent(getString(R.string.edit_profile_fetch_failed_message)))
                    finishActivityEvent.postValue(EmptyEventObject)
                })
        )
    }

    fun onBirthdayClick() {
        val birthday = profile.birthday
        if (birthday == null) {
            showBirthdayPickerEvent.postValue(
                ShowBirthdayPickerEvent(INITIAL_BIRTH_YEAR, INITIAL_BIRTH_MONTH, INITIAL_BIRTH_DAY)
            )
        } else {
            // 生年月日が選択済みであれば、それを初期値として生年月日選択ダイアログを表示させる
            showBirthdayPickerEvent.postValue(
                ShowBirthdayPickerEvent(birthday.year, birthday.month, birthday.day)
            )
        }
    }

    fun onBirthdaySelected(year: Int, month: Int, day: Int) {
        profile.birthday = Profile.Birthday(year, month, day)
    }

    fun onPrefectureClick() {
        showPrefectureSelectionEvent.postValue(EmptyEventObject)
    }

    fun onPrefectureSelected(prefecture: Prefecture) {
        profile.prefecture = prefecture
    }

    fun onLastNameUnfocused() {
        // 各入力欄からフォーカスが外れたタイミングでバリデーションを実行する
        val error = ProfileValidator(profile).validateLastName()
        validationErrors.set(validationErrors.get().copy(lastNameError = error))
    }

    fun onFirstNameUnfocused() {
        val error = ProfileValidator(profile).validateFirstName()
        validationErrors.set(validationErrors.get().copy(firstNameError = error))
    }

    fun onKanaLastNameUnfocused() {
        val error = ProfileValidator(profile).validateKanaLastName()
        validationErrors.set(validationErrors.get().copy(kanaLastNameError = error))
    }

    fun onKanaFirstNameUnfocused() {
        val error = ProfileValidator(profile).validateKanaFirstName()
        validationErrors.set(validationErrors.get().copy(kanaFirstNameError = error))
    }

    fun onPhoneNumberUnfocused() {
        val error = ProfileValidator(profile).validatePhoneNumber()
        validationErrors.set(validationErrors.get().copy(phoneNumberError = error))
    }

    fun onZipCodeUnfocused() {
        val error = ProfileValidator(profile).validateZipCode()
        validationErrors.set(validationErrors.get().copy(zipCodeError = error))
    }

    fun onCitiesUnfocused() {
        val error = ProfileValidator(profile).validateCities()
        validationErrors.set(validationErrors.get().copy(citiesError = error))
    }

    fun onAddress1Unfocused() {
        val error = ProfileValidator(profile).validateAddress1()
        validationErrors.set(validationErrors.get().copy(address1Error = error))
    }

    fun onAddress2Unfocused() {
        val error = ProfileValidator(profile).validateAddress2()
        validationErrors.set(validationErrors.get().copy(address2Error = error))
    }

    fun onSaveButtonClick() {
        if (!validateProfile()) {
            showSnackbarEvent.postValue(ShowSnackbarEvent(getString(R.string.edit_profile_invalid_message)))
            return
        }

        updateProfile()
    }

    private fun validateProfile(): Boolean {
        val validator = ProfileValidator(profile)
        validationErrors.set(
            ValidationErrors(
                validator.validateLastName(),
                validator.validateFirstName(),
                validator.validateKanaLastName(),
                validator.validateKanaFirstName(),
                validator.validatePhoneNumber(),
                validator.validateBirthday(),
                validator.validateZipCode(),
                validator.validatePrefecture(),
                validator.validateCities(),
                validator.validateAddress1(),
                validator.validateAddress2()
            )
        )
        return !validator.anyError()
    }

    private fun updateProfile() {
        viewState.set(ViewState.Updating)
        disposables.add(
            repository.updateProfile(profile)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    showToastEvent.postValue(ShowToastEvent(getString(R.string.edit_profile_updated_message)))
                    finishActivityEvent.postValue(EmptyEventObject)
                }, {
                    viewState.set(ViewState.Fetched)
                    showSnackbarEvent.postValue(
                        ShowSnackbarEvent(getString(R.string.edit_profile_update_failed_message))
                    )
                })
        )
    }

    object EmptyEventObject

    data class ShowBirthdayPickerEvent(val year: Int, val month: Int, val day: Int)

    data class ShowToastEvent(val text: String)

    data class ShowSnackbarEvent(val text: String)

    enum class ViewState {
        Fetching, Fetched, Updating;

        val contentVisibility get() = if (this == Fetched || this == Updating) View.VISIBLE else View.GONE
        val progressBarVisibility get() = if (this == Fetched || this == Updating) View.GONE else View.VISIBLE

        val updateButtonVisibility get() = if (this == Fetched) View.VISIBLE else View.GONE
        val updateProgressBarVisibility get() = if (this == Updating) View.VISIBLE else View.GONE
    }

    data class ValidationErrors(
        val lastNameError: ProfileValidator.Error? = null,
        val firstNameError: ProfileValidator.Error? = null,
        val kanaLastNameError: ProfileValidator.Error? = null,
        val kanaFirstNameError: ProfileValidator.Error? = null,
        val phoneNumberError: ProfileValidator.Error? = null,
        val birthdayError: ProfileValidator.Error? = null,
        val zipCodeError: ProfileValidator.Error? = null,
        val prefectureError: ProfileValidator.Error? = null,
        val citiesError: ProfileValidator.Error? = null,
        val address1Error: ProfileValidator.Error? = null,
        val address2Error: ProfileValidator.Error? = null
    )
}
