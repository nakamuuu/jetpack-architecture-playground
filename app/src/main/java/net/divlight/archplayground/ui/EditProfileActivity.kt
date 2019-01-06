package net.divlight.archplayground.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import net.divlight.archplayground.R
import net.divlight.archplayground.data.Prefecture
import net.divlight.archplayground.databinding.ActivityEditProfileBinding
import net.divlight.archplayground.extension.observe
import net.divlight.archplayground.extension.observeNonNull
import net.divlight.archplayground.ui.common.DatePickerDialogFragment
import net.divlight.archplayground.ui.common.SelectPrefectureDialogFragment
import net.divlight.archplayground.ui.common.getStateAwareViewModel

class EditProfileActivity : AppCompatActivity(),
    DatePickerDialog.OnDateSetListener,
    SelectPrefectureDialogFragment.OnPrefectureSelectedListener {
    private lateinit var viewModel: EditProfileViewModel
    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getStateAwareViewModel(EditProfileViewModel::class.java, savedInstanceState).also {
            it.showBirthdayPickerEvent.observeNonNull(this) { event ->
                DatePickerDialogFragment.newInstance(event.year, event.month - 1, event.day)
                    .show(supportFragmentManager, DatePickerDialogFragment.TAG)
            }
            it.showPrefectureSelectionEvent.observe(this) {
                SelectPrefectureDialogFragment.newInstance()
                    .show(supportFragmentManager, SelectPrefectureDialogFragment.TAG)
            }
            it.showToastEvent.observeNonNull(this) { event ->
                Toast.makeText(this, event.text, Toast.LENGTH_SHORT).show()
            }
            it.showSnackbarEvent.observeNonNull(this) { event ->
                Snackbar.make(binding.coordinatorLayout, event.text, Snackbar.LENGTH_LONG).show()
            }
            it.finishActivityEvent.observe(this) {
                finish()
            }
        }
        lifecycle.addObserver(viewModel)

        binding = DataBindingUtil.setContentView<ActivityEditProfileBinding>(
            this,
            R.layout.activity_edit_profile
        ).also { it.viewModel = this.viewModel }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //
    // Options Menu
    //

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            NavUtils.navigateUpFromSameTask(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    //
    // DatePickerDialog.OnDateSetListener
    //

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        viewModel.onBirthdaySelected(year, month + 1, dayOfMonth)
    }

    //
    // SelectPrefectureDialogFragment.OnPrefectureSelectedListener
    //

    override fun onPrefectureSelected(prefecture: Prefecture) {
        viewModel.onPrefectureSelected(prefecture)
    }
}
