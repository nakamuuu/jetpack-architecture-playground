package net.divlight.archplayground.data

import android.content.Context
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import net.divlight.archplayground.BR
import net.divlight.archplayground.R
import java.io.Serializable

data class Profile(
    private var _lastName: String = "",
    private var _firstName: String = "",
    private var _kanaLastName: String = "",
    private var _kanaFirstName: String = "",
    private var _phoneNumber: String = "",
    private var _birthday: Birthday? = null,
    private var _zipCode: String = "",
    private var _prefecture: Prefecture? = null,
    private var _cities: String = "",
    private var _address1: String = "",
    private var _address2: String = ""
) : BaseObservable(), Serializable {
    companion object {
        const val CITIES_MAX_LENGTH = 100
        const val ADDRESS1_MAX_LENGTH = 200
        const val ADDRESS2_MAX_LENGTH = 200
    }

    var lastName: String
        @Bindable get() = _lastName
        set(value) {
            if (value != _lastName) {
                _lastName = value
                notifyPropertyChanged(BR.lastName)
            }
        }
    var firstName: String
        @Bindable get() = _firstName
        set(value) {
            if (value != _firstName) {
                _firstName = value
                notifyPropertyChanged(BR.firstName)
            }
        }
    var kanaLastName: String
        @Bindable get() = _kanaLastName
        set(value) {
            if (value != _kanaLastName) {
                _kanaLastName = value
                notifyPropertyChanged(BR.kanaLastName)
            }
        }
    var kanaFirstName: String
        @Bindable get() = _kanaFirstName
        set(value) {
            if (value != _kanaFirstName) {
                _kanaFirstName = value
                notifyPropertyChanged(BR.kanaFirstName)
            }
        }
    var phoneNumber: String
        @Bindable get() = _phoneNumber
        set(value) {
            if (value != _phoneNumber) {
                _phoneNumber = value
                notifyPropertyChanged(BR.phoneNumber)
            }
        }
    var birthday: Birthday?
        @Bindable get() = _birthday
        set(value) {
            if (value != _birthday) {
                _birthday = value
                notifyPropertyChanged(BR.birthday)
            }
        }
    var zipCode: String
        @Bindable get() = _zipCode
        set(value) {
            if (value != _zipCode) {
                _zipCode = value
                notifyPropertyChanged(BR.zipCode)
            }
        }
    var prefecture: Prefecture?
        @Bindable get() = _prefecture
        set(value) {
            if (value != _prefecture) {
                _prefecture = value
                notifyPropertyChanged(BR.prefecture)
            }
        }
    var cities: String
        @Bindable get() = _cities
        set(value) {
            if (value != _cities) {
                _cities = value
                notifyPropertyChanged(BR.cities)
            }
        }
    var address1: String
        @Bindable get() = _address1
        set(value) {
            if (value != _address1) {
                _address1 = value
                notifyPropertyChanged(BR.address1)
            }
        }
    var address2: String
        @Bindable get() = _address2
        set(value) {
            if (value != _address2) {
                _address2 = value
                notifyPropertyChanged(BR.address2)
            }
        }

    fun set(from: Profile) {
        lastName = from.lastName
        firstName = from.firstName
        kanaLastName = from.kanaLastName
        kanaFirstName = from.kanaFirstName
        phoneNumber = from.phoneNumber
        birthday = from.birthday
        zipCode = from.zipCode
        prefecture = from.prefecture
        cities = from.cities
        address1 = from.address1
        address2 = from.address2
    }

    data class Birthday(val year: Int, val month: Int, val day: Int) : Serializable {
        fun getDisplayString(context: Context): String = context.resources
            .getString(R.string.birthday_format, year, month, day)
    }
}
