package net.divlight.archplayground.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileValidatorTest {
    private val validProfile = Profile(
        "泥井戸", "太郎", "ドロイド", "タロウ", "08012345678", Profile.Birthday(1990, 1, 1),
        "1008111", Prefecture.Tokyo, "千代田区", "千代田１−１", "千代田マンション101"
    )

    @Test
    fun testAnyErrorWithValidCase() {
        assertThat(ProfileValidator(validProfile).anyError())
            .`as`("保存可能なユーザー情報なら false が返る")
            .isEqualTo(false)
    }

    @Test
    fun testAnyErrorWithEmptyCase() {
        val profile = Profile()
        assertThat(ProfileValidator(profile).anyError())
            .`as`("各項目が未入力なら true が返る")
            .isEqualTo(true)
    }

    @Test
    fun testAnyErrorWithInvalidCase() {
        val invalidPhoneNumber = "080123456789"
        val invalidZipCode = "12345678"
        val tooLongCities = "a".repeat(Profile.CITIES_MAX_LENGTH + 1)
        val tooLongAddress1 = "a".repeat(Profile.ADDRESS1_MAX_LENGTH + 1)
        val tooLongAddress2 = "a".repeat(Profile.ADDRESS2_MAX_LENGTH + 1)
        val profile = validProfile.copy(
            _phoneNumber = invalidPhoneNumber,
            _zipCode = invalidZipCode,
            _cities = tooLongCities,
            _address1 = tooLongAddress1,
            _address2 = tooLongAddress2
        )
        assertThat(ProfileValidator(profile).anyError())
            .`as`("各項目が不正なら true が返る")
            .isEqualTo(true)
    }

    @Test
    fun testValidateLastName() {
        assertThat(ProfileValidator(validProfile).validateLastName())
            .`as`("姓が入力済みならエラーは返らない")
            .isNull()
        val emptyLastNameProfile = validProfile.copy(_lastName = "")
        assertThat(ProfileValidator(emptyLastNameProfile).validateLastName())
            .`as`("姓が未入力なら LAST_NAME_EMPTY のエラーが返る")
            .isEqualTo(ProfileValidator.Error.LAST_NAME_EMPTY)
    }

    @Test
    fun testValidateFirstName() {
        assertThat(ProfileValidator(validProfile).validateFirstName())
            .`as`("名が入力済みならエラーは返らない")
            .isNull()
        val emptyFirstNameProfile = validProfile.copy(_firstName = "")
        assertThat(ProfileValidator(emptyFirstNameProfile).validateFirstName())
            .`as`("名が未入力なら FIRST_NAME_EMPTY のエラーが返る")
            .isEqualTo(ProfileValidator.Error.FIRST_NAME_EMPTY)
    }

    @Test
    fun testValidateKanaLastName() {
        assertThat(ProfileValidator(validProfile).validateKanaLastName())
            .`as`("セイが入力済みならエラーは返らない")
            .isNull()
        val emptyKanaLastNameProfile = validProfile.copy(_kanaLastName = "")
        assertThat(ProfileValidator(emptyKanaLastNameProfile).validateKanaLastName())
            .`as`("セイが未入力なら KANA_LAST_NAME_EMPTY のエラーが返る")
            .isEqualTo(ProfileValidator.Error.KANA_LAST_NAME_EMPTY)
    }

    @Test
    fun testValidateKanaFirstName() {
        assertThat(ProfileValidator(validProfile).validateKanaFirstName())
            .`as`("メイが入力済みならエラーは返らない")
            .isNull()
        val emptyKanaFirstNameProfile = validProfile.copy(_kanaFirstName = "")
        assertThat(ProfileValidator(emptyKanaFirstNameProfile).validateKanaFirstName())
            .`as`("メイが未入力なら KANA_FIRST_NAME_EMPTY のエラーが返る")
            .isEqualTo(ProfileValidator.Error.KANA_FIRST_NAME_EMPTY)
    }

    @Test
    fun testValidatePhoneNumber() {
        assertThat(ProfileValidator(validProfile).validatePhoneNumber())
            .`as`("電話番号が入力済みならエラーは返らない")
            .isNull()
        val emptyPhoneNumberProfile = validProfile.copy(_phoneNumber = "")
        assertThat(ProfileValidator(emptyPhoneNumberProfile).validatePhoneNumber())
            .`as`("電話番号が未入力なら PHONE_NUMBER_EMPTY のエラーが返る")
            .isEqualTo(ProfileValidator.Error.PHONE_NUMBER_EMPTY)
        val invalidPhoneNumberProfile = validProfile.copy(_phoneNumber = "080123456789")
        assertThat(ProfileValidator(invalidPhoneNumberProfile).validatePhoneNumber())
            .`as`("電話番号が不正であれば PHONE_NUMBER_INVALID のエラーが返る")
            .isEqualTo(ProfileValidator.Error.PHONE_NUMBER_INVALID)
    }

    @Test
    fun testValidateBirthday() {
        assertThat(ProfileValidator(validProfile).validateBirthday())
            .`as`("生年月日が選択済みならエラーは返らない")
            .isNull()
        val emptyBirthday = validProfile.copy(_birthday = null)
        assertThat(ProfileValidator(emptyBirthday).validateBirthday())
            .`as`("生年月日が未選択なら BIRTHDAY_EMPTY のエラーが返る")
            .isEqualTo(ProfileValidator.Error.BIRTHDAY_EMPTY)
    }

    @Test
    fun testValidateZipCode() {
        assertThat(ProfileValidator(validProfile).validateZipCode())
            .`as`("郵便番号が入力済みならエラーは返らない")
            .isNull()
        val emptyZipCodeProfile = validProfile.copy(_zipCode = "")
        assertThat(ProfileValidator(emptyZipCodeProfile).validateZipCode())
            .`as`("郵便番号が未入力なら ZIP_CODE_EMPTY のエラーが返る")
            .isEqualTo(ProfileValidator.Error.ZIP_CODE_EMPTY)
        val invalidZipCodeProfile = validProfile.copy(_zipCode = "12345678")
        assertThat(ProfileValidator(invalidZipCodeProfile).validateZipCode())
            .`as`("郵便番号が不正であれば ZIP_CODE_INVALID のエラーが返る")
            .isEqualTo(ProfileValidator.Error.ZIP_CODE_INVALID)
    }

    @Test
    fun testValidatePrefecture() {
        assertThat(ProfileValidator(validProfile).validatePrefecture())
            .`as`("都道府県が選択済みならエラーは返らない")
            .isNull()
        val emptyPrefecture = validProfile.copy(_prefecture = null)
        assertThat(ProfileValidator(emptyPrefecture).validatePrefecture())
            .`as`("都道府県が未選択なら PREFECTURE_EMPTY のエラーが返る")
            .isEqualTo(ProfileValidator.Error.PREFECTURE_EMPTY)
    }

    @Test
    fun testValidateCities() {
        assertThat(ProfileValidator(validProfile).validateCities())
            .`as`("市区町村が入力済みならエラーは返らない")
            .isNull()
        val emptyCitiesProfile = validProfile.copy(_cities = "")
        assertThat(ProfileValidator(emptyCitiesProfile).validateCities())
            .`as`("市区町村が未入力なら CITIES_EMPTY のエラーが返る")
            .isEqualTo(ProfileValidator.Error.CITIES_EMPTY)
        val tooLongCities = "a".repeat(Profile.CITIES_MAX_LENGTH + 1)
        val tooLongCitiesProfile = validProfile.copy(_cities = tooLongCities)
        assertThat(ProfileValidator(tooLongCitiesProfile).validateCities())
            .`as`("市区町村の字数が不正であれば CITIES_LENGTH のエラーが返る")
            .isEqualTo(ProfileValidator.Error.CITIES_LENGTH)
    }

    @Test
    fun testValidateAddress1() {
        assertThat(ProfileValidator(validProfile).validateAddress1())
            .`as`("町名番地が入力済みならエラーは返らない")
            .isNull()
        val emptyAddress1Profile = validProfile.copy(_address1 = "")
        assertThat(ProfileValidator(emptyAddress1Profile).validateAddress1())
            .`as`("町名番地が未入力なら ADDRESS1_EMPTY のエラーが返る")
            .isEqualTo(ProfileValidator.Error.ADDRESS1_EMPTY)
        val tooLongAddress1 = "a".repeat(Profile.ADDRESS1_MAX_LENGTH + 1)
        val tooLongAddress1Profile = validProfile.copy(_address1 = tooLongAddress1)
        assertThat(ProfileValidator(tooLongAddress1Profile).validateAddress1())
            .`as`("町名番地の字数が不正であれば ADDRESS1_LENGTH のエラーが返る")
            .isEqualTo(ProfileValidator.Error.ADDRESS1_LENGTH)
    }

    @Test
    fun testValidateAddress2() {
        assertThat(ProfileValidator(validProfile).validateAddress2())
            .`as`("建物名・部屋番号が入力済みならエラーは返らない")
            .isNull()
        val emptyAddress2Profile = validProfile.copy(_address2 = "")
        assertThat(ProfileValidator(emptyAddress2Profile).validateAddress2())
            .`as`("建物名・部屋番号が未入力でもエラーは返らない")
            .isNull()
        val tooLongAddress2 = "a".repeat(Profile.ADDRESS2_MAX_LENGTH + 1)
        val tooLongAddress2Profile = validProfile.copy(_address2 = tooLongAddress2)
        assertThat(ProfileValidator(tooLongAddress2Profile).validateAddress2())
            .`as`("建物名・部屋番号の字数が不正であれば ADDRESS2_LENGTH のエラーが返る")
            .isEqualTo(ProfileValidator.Error.ADDRESS2_LENGTH)
    }
}
