package net.divlight.archplayground.data

import android.content.Context
import androidx.annotation.StringRes
import net.divlight.archplayground.R

class ProfileValidator(private val profile: Profile) {
    fun anyError() = listOfNotNull(
        validateLastName(),
        validateFirstName(),
        validateKanaLastName(),
        validateKanaFirstName(),
        validatePhoneNumber(),
        validateBirthday(),
        validateZipCode(),
        validatePrefecture(),
        validateCities(),
        validateAddress1(),
        validateAddress2()
    ).isNotEmpty()

    fun validateLastName() = when {
        profile.lastName.isEmpty() -> Error.LAST_NAME_EMPTY
        else -> null
    }

    fun validateFirstName() = when {
        profile.firstName.isEmpty() -> Error.FIRST_NAME_EMPTY
        else -> null
    }

    fun validateKanaLastName() = when {
        profile.kanaLastName.isEmpty() -> Error.KANA_LAST_NAME_EMPTY
        else -> null
    }

    fun validateKanaFirstName() = when {
        profile.kanaFirstName.isEmpty() -> Error.KANA_FIRST_NAME_EMPTY
        else -> null
    }

    fun validatePhoneNumber() = when {
        profile.phoneNumber.isEmpty() -> Error.PHONE_NUMBER_EMPTY
        !profile.phoneNumber.matches("^0[^0][0-9]{8,9}$".toRegex()) -> Error.PHONE_NUMBER_INVALID
        else -> null
    }

    fun validateBirthday() = when {
        profile.birthday == null -> Error.BIRTHDAY_EMPTY
        else -> null
    }

    fun validateZipCode() = when {
        profile.zipCode.isEmpty() -> Error.ZIP_CODE_EMPTY
        !profile.zipCode.matches("^[0-9]{7}$".toRegex()) -> Error.ZIP_CODE_INVALID
        else -> null
    }

    fun validatePrefecture() = when {
        profile.prefecture == null -> Error.PREFECTURE_EMPTY
        else -> null
    }

    fun validateCities() = when {
        profile.cities.isEmpty() -> Error.CITIES_EMPTY
        profile.cities.length > Profile.CITIES_MAX_LENGTH -> Error.CITIES_LENGTH
        else -> null
    }

    fun validateAddress1() = when {
        profile.address1.isEmpty() -> Error.ADDRESS1_EMPTY
        profile.address1.length > Profile.ADDRESS1_MAX_LENGTH -> Error.ADDRESS1_LENGTH
        else -> null
    }

    fun validateAddress2() = when {
        profile.address2.length > Profile.ADDRESS2_MAX_LENGTH -> Error.ADDRESS2_LENGTH
        else -> null
    }

    enum class Error(@StringRes private val resId: Int, private vararg val formatArgs: Any) {
        LAST_NAME_EMPTY(R.string.edit_profile_error_last_name_empty),
        FIRST_NAME_EMPTY(R.string.edit_profile_error_first_name_empty),
        KANA_LAST_NAME_EMPTY(R.string.edit_profile_error_kana_last_name_empty),
        KANA_FIRST_NAME_EMPTY(R.string.edit_profile_error_kana_first_name_empty),
        PHONE_NUMBER_EMPTY(R.string.edit_profile_error_phone_number_empty),
        PHONE_NUMBER_INVALID(R.string.edit_profile_error_phone_number_invalid),
        BIRTHDAY_EMPTY(R.string.edit_profile_error_birthday_empty),
        ZIP_CODE_EMPTY(R.string.edit_profile_error_zip_code_empty),
        ZIP_CODE_INVALID(R.string.edit_profile_error_zip_code_invalid),
        PREFECTURE_EMPTY(R.string.edit_profile_error_prefecture_empty),
        CITIES_EMPTY(R.string.edit_profile_error_cities_empty),
        CITIES_LENGTH(R.string.edit_profile_error_cities_length, Profile.CITIES_MAX_LENGTH),
        ADDRESS1_EMPTY(R.string.edit_profile_error_address1_empty),
        ADDRESS1_LENGTH(R.string.edit_profile_error_address1_length, Profile.ADDRESS1_MAX_LENGTH),
        ADDRESS2_LENGTH(R.string.edit_profile_error_address2_length, Profile.ADDRESS2_MAX_LENGTH);

        fun getErrorText(context: Context): String = context.getString(resId, *formatArgs)
    }
}
