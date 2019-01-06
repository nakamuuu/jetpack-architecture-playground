package net.divlight.archplayground.ui.common

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.divlight.archplayground.R
import net.divlight.archplayground.data.Prefecture

class SelectPrefectureDialogFragment : AppCompatDialogFragment() {
    companion object {
        @JvmField val TAG: String = SelectPrefectureDialogFragment::class.java.simpleName

        fun newInstance() = SelectPrefectureDialogFragment()
    }

    private var listener: OnPrefectureSelectedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnPrefectureSelectedListener
    }

    override fun onDetach() {
        listener = null
        super.onDetach()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) = MaterialAlertDialogBuilder(requireContext())
        .setItems(R.array.prefectures) { _, which ->
            listener?.onPrefectureSelected(Prefecture.values()[which - 1])
        }
        .create()

    interface OnPrefectureSelectedListener {
        fun onPrefectureSelected(prefecture: Prefecture)
    }
}
