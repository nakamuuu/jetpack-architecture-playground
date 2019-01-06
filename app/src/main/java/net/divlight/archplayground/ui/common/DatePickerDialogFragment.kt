package net.divlight.archplayground.ui.common

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment

class DatePickerDialogFragment : AppCompatDialogFragment() {
    companion object {
        @JvmField val TAG: String = DatePickerDialogFragment::class.java.simpleName

        private const val ARGS_YEAR = "year"
        private const val ARGS_MONTH_OF_YEAR = "month_of_year"
        private const val ARGS_DAY_OF_MONTH = "day_of_month"

        @JvmStatic
        fun newInstance(year: Int, monthOfYear: Int, dayOfMonth: Int) =
            DatePickerDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARGS_YEAR, year)
                    putInt(ARGS_MONTH_OF_YEAR, monthOfYear)
                    putInt(ARGS_DAY_OF_MONTH, dayOfMonth)
                }
            }
    }

    private var onDateSetListener: DatePickerDialog.OnDateSetListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onDateSetListener = context as? DatePickerDialog.OnDateSetListener
    }

    override fun onDetach() {
        onDateSetListener = null
        super.onDetach()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = DatePickerDialog(
        requireContext(),
        onDateSetListener,
        arguments?.getInt(ARGS_YEAR) ?: 1990,
        arguments?.getInt(ARGS_MONTH_OF_YEAR) ?: 1,
        arguments?.getInt(ARGS_DAY_OF_MONTH) ?: 1
    )
}
