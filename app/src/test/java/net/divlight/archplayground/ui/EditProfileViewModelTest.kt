package net.divlight.archplayground.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import net.divlight.archplayground.data.Prefecture
import net.divlight.archplayground.data.Profile
import net.divlight.archplayground.data.ProfileRepository
import net.divlight.archplayground.ui.EditProfileViewModel.EmptyEventObject
import net.divlight.archplayground.ui.EditProfileViewModel.ShowBirthdayPickerEvent
import net.divlight.archplayground.ui.EditProfileViewModel.ShowSnackbarEvent
import net.divlight.archplayground.ui.EditProfileViewModel.ShowToastEvent
import net.divlight.archplayground.ui.EditProfileViewModel.ViewState
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditProfileViewModelTest {
    private val validProfile = Profile(
        "泥井戸", "太郎", "ドロイド", "タロウ", "08012345678", Profile.Birthday(1990, 1, 1),
        "1008111", Prefecture.Tokyo, "千代田区", "千代田１−１", "千代田マンション101"
    )
    private val invalidProfile = Profile(
        // address2のプロパティのみ空欄を許容しているので文字数制限に当たる文字列をセットする
        _address2 = "a".repeat(Profile.ADDRESS2_MAX_LENGTH + 1)
    )

    private val repository = mock<ProfileRepository>()
    private val viewModel = EditProfileViewModel(ApplicationProvider.getApplicationContext(), repository)
    private val showBirthdayPickerEventObserver = mock<Observer<ShowBirthdayPickerEvent>>()
    private val showPrefectureSelectionEventObserver = mock<Observer<EmptyEventObject>>()
    private val showToastEventObserver = mock<Observer<ShowToastEvent>>()
    private val showSnackbarEventObserver = mock<Observer<ShowSnackbarEvent>>()
    private val finishActivityEventObserver = mock<Observer<EmptyEventObject>>()

    @Before
    fun setup() {
        viewModel.showBirthdayPickerEvent.observeForever(showBirthdayPickerEventObserver)
        viewModel.showPrefectureSelectionEvent.observeForever(showPrefectureSelectionEventObserver)
        viewModel.showToastEvent.observeForever(showToastEventObserver)
        viewModel.showSnackbarEvent.observeForever(showSnackbarEventObserver)
        viewModel.finishActivityEvent.observeForever(finishActivityEventObserver)
    }

    @Test
    fun testViewState() {
        assertThat(ViewState.Fetching.contentVisibility).isEqualTo(View.GONE)
        assertThat(ViewState.Fetching.progressBarVisibility).isEqualTo(View.VISIBLE)
        assertThat(ViewState.Fetched.contentVisibility).isEqualTo(View.VISIBLE)
        assertThat(ViewState.Fetched.progressBarVisibility).isEqualTo(View.GONE)
        assertThat(ViewState.Fetched.updateButtonVisibility).isEqualTo(View.VISIBLE)
        assertThat(ViewState.Fetched.updateProgressBarVisibility).isEqualTo(View.GONE)
        assertThat(ViewState.Updating.contentVisibility).isEqualTo(View.VISIBLE)
        assertThat(ViewState.Updating.progressBarVisibility).isEqualTo(View.GONE)
        assertThat(ViewState.Updating.updateButtonVisibility).isEqualTo(View.GONE)
        assertThat(ViewState.Updating.updateProgressBarVisibility).isEqualTo(View.VISIBLE)
    }

    @Test
    fun testFetchProfileWithSuccess() {
        val profile = validProfile.copy()
        val scheduler = TestScheduler()
        whenever(repository.fetchProfile())
            .thenReturn(Single.just(profile).subscribeOn(scheduler))

        viewModel.onCreate()
        assertThat(viewModel.viewState.get())
            .`as`("ユーザー情報の読み込み中はFetchingのViewStateになる")
            .isEqualTo(ViewState.Fetching)

        scheduler.triggerActions()
        assertThat(viewModel.viewState.get())
            .`as`("ユーザー情報の読み込みが完了した後はFetchedのViewStateになる")
            .isEqualTo(ViewState.Fetched)
        assertThat(viewModel.profile)
            .`as`("読み込まれたユーザー情報が表示される")
            .isEqualTo(profile)
    }

    @Test
    fun testFetchProfileWithError() {
        val scheduler = TestScheduler()
        whenever(repository.fetchProfile())
            .thenReturn(Single.error<Profile>(IllegalStateException()).subscribeOn(scheduler))

        viewModel.onCreate()
        assertThat(viewModel.viewState.get())
            .`as`("ユーザー情報の読み込み中はFetchingのViewStateになる")
            .isEqualTo(ViewState.Fetching)

        scheduler.triggerActions()
        verify(
            showToastEventObserver,
            times(1).description("ユーザー情報の読み込みに失敗した場合はToastが表示される")
        ).onChanged(any())
        verify(
            finishActivityEventObserver,
            times(1).description("ユーザー情報の読み込みに失敗した場合は画面が閉じる")
        ).onChanged(EmptyEventObject)
    }

    @Test
    fun testOnBirthdaySelected() {
        viewModel.profile.set(Profile())

        viewModel.onBirthdayClick()
        verify(
            showBirthdayPickerEventObserver,
            times(1).description("生年月日欄を押下すると生年月日選択ダイアログが開く")
        ).onChanged(any())

        val expected = Profile.Birthday(1990, 1, 1)
        viewModel.onBirthdaySelected(expected.year, expected.month, expected.day)
        assertThat(viewModel.profile.birthday)
            .`as`("選択した生年月日がViewModelで保持するユーザー情報に反映されている")
            .isEqualTo(expected)
    }

    @Test
    fun testOnPrefectureSelected() {
        viewModel.profile.set(Profile())

        viewModel.onPrefectureClick()
        verify(
            showPrefectureSelectionEventObserver,
            times(1).description("都道府県欄を押下すると都道府県選択ダイアログが開く")
        ).onChanged(EmptyEventObject)

        viewModel.onPrefectureSelected(Prefecture.Chiba)
        assertThat(viewModel.profile.prefecture)
            .`as`("選択した都道府県がViewModelで保持するユーザー情報に反映されている")
            .isEqualTo(Prefecture.Chiba)
    }

    @Test
    fun testValidateOnFocusChange() {
        viewModel.profile.set(invalidProfile.copy())
        viewModel.onLastNameUnfocused()
        viewModel.onFirstNameUnfocused()
        viewModel.onKanaLastNameUnfocused()
        viewModel.onKanaFirstNameUnfocused()
        viewModel.onPhoneNumberUnfocused()
        viewModel.onZipCodeUnfocused()
        viewModel.onCitiesUnfocused()
        viewModel.onAddress1Unfocused()
        viewModel.onAddress2Unfocused()

        // 各入力欄からフォーカスが外れたタイミングで入力内容が不正であればエラーが表示される
        assertThat(viewModel.validationErrors.get().lastNameError).isNotNull
        assertThat(viewModel.validationErrors.get().firstNameError).isNotNull
        assertThat(viewModel.validationErrors.get().kanaFirstNameError).isNotNull
        assertThat(viewModel.validationErrors.get().kanaLastNameError).isNotNull
        assertThat(viewModel.validationErrors.get().phoneNumberError).isNotNull
        assertThat(viewModel.validationErrors.get().zipCodeError).isNotNull
        assertThat(viewModel.validationErrors.get().citiesError).isNotNull
        assertThat(viewModel.validationErrors.get().address1Error).isNotNull
        assertThat(viewModel.validationErrors.get().address2Error).isNotNull

        viewModel.profile.set(validProfile.copy())
        viewModel.onLastNameUnfocused()
        viewModel.onFirstNameUnfocused()
        viewModel.onKanaLastNameUnfocused()
        viewModel.onKanaFirstNameUnfocused()
        viewModel.onPhoneNumberUnfocused()
        viewModel.onZipCodeUnfocused()
        viewModel.onCitiesUnfocused()
        viewModel.onAddress1Unfocused()
        viewModel.onAddress2Unfocused()

        // 各入力欄からフォーカスが外れたタイミングで入力内容が適切であればエラーは表示されない
        assertThat(viewModel.validationErrors.get().lastNameError).isNull()
        assertThat(viewModel.validationErrors.get().firstNameError).isNull()
        assertThat(viewModel.validationErrors.get().kanaFirstNameError).isNull()
        assertThat(viewModel.validationErrors.get().kanaLastNameError).isNull()
        assertThat(viewModel.validationErrors.get().phoneNumberError).isNull()
        assertThat(viewModel.validationErrors.get().zipCodeError).isNull()
        assertThat(viewModel.validationErrors.get().citiesError).isNull()
        assertThat(viewModel.validationErrors.get().address1Error).isNull()
        assertThat(viewModel.validationErrors.get().address2Error).isNull()
    }

    @Test
    fun testUpdateProfileWithSuccess() {
        val scheduler = TestScheduler()
        whenever(repository.updateProfile(any()))
            .thenReturn(Completable.complete().subscribeOn(scheduler))

        viewModel.profile.set(validProfile.copy())

        viewModel.onSaveButtonClick()
        assertThat(viewModel.viewState.get())
            .`as`("ユーザー情報の保存中はUpdatingのViewStateになる")
            .isEqualTo(ViewState.Updating)
        verify(
            repository,
            times(1).description("ViewModelで保持するユーザー情報が保存させる")
        ).updateProfile(validProfile)

        scheduler.triggerActions()
        verify(
            showToastEventObserver,
            times(1).description("ユーザー情報の保存が完了するとToastが表示される")
        ).onChanged(any())
        verify(
            finishActivityEventObserver,
            times(1).description("ユーザー情報の保存が完了すると画面が閉じる")
        ).onChanged(EmptyEventObject)
    }

    @Test
    fun testUpdateProfileWithInvalid() {
        viewModel.profile.set(invalidProfile.copy())
        viewModel.onSaveButtonClick()
        verify(
            showSnackbarEventObserver,
            times(1).description("入力内容が不正な状態で保存を行おうとするとSnackbarが表示される")
        ).onChanged(any())
        verify(
            repository,
            never().description("入力内容が不正な場合はユーザー情報の保存処理は行われない")
        ).updateProfile(any())

        // 保存を行おうとしたタイミングで入力内容が不正であればエラーが表示される
        assertThat(viewModel.validationErrors.get().lastNameError).isNotNull
        assertThat(viewModel.validationErrors.get().firstNameError).isNotNull
        assertThat(viewModel.validationErrors.get().kanaFirstNameError).isNotNull
        assertThat(viewModel.validationErrors.get().kanaLastNameError).isNotNull
        assertThat(viewModel.validationErrors.get().phoneNumberError).isNotNull
        assertThat(viewModel.validationErrors.get().zipCodeError).isNotNull
        assertThat(viewModel.validationErrors.get().prefectureError).isNotNull
        assertThat(viewModel.validationErrors.get().citiesError).isNotNull
        assertThat(viewModel.validationErrors.get().address1Error).isNotNull
        assertThat(viewModel.validationErrors.get().address2Error).isNotNull
    }

    @Test
    fun testUpdateProfileWithError() {
        val scheduler = TestScheduler()
        whenever(repository.updateProfile(any()))
            .thenReturn(Completable.error(IllegalStateException()).subscribeOn(scheduler))

        viewModel.profile.set(validProfile.copy())

        viewModel.onSaveButtonClick()
        assertThat(viewModel.viewState.get())
            .`as`("ユーザー情報の保存中はUpdatingのViewStateになる")
            .isEqualTo(ViewState.Updating)
        verify(
            repository,
            times(1).description("ViewModelで保持するユーザー情報が保存させる")
        ).updateProfile(validProfile)

        scheduler.triggerActions()
        assertThat(viewModel.viewState.get())
            .`as`("ユーザー情報の保存に失敗するとはFetchedのViewStateに戻る")
            .isEqualTo(ViewState.Fetched)
        verify(
            showSnackbarEventObserver,
            times(1).description("ユーザー情報の保存に失敗するとSnackbarが表示される")
        ).onChanged(any())
    }

    @Test
    fun testRestoreInstanceState() {
        val profile = validProfile.copy(_phoneNumber = "07012345678")
        val oldViewModel = EditProfileViewModel(ApplicationProvider.getApplicationContext(), repository)
        oldViewModel.profile.set(profile)
        viewModel.restoreInstanceState(Bundle().also(oldViewModel::saveInstanceState))
        assertThat(viewModel.profile)
            .`as`("ユーザー情報がBundleからリストアされている")
            .isEqualTo(profile)
        assertThat(viewModel.viewState.get())
            .`as`("ユーザー情報が復元された後はFetchingのViewStateになる")
            .isEqualTo(ViewState.Fetched)
    }
}
