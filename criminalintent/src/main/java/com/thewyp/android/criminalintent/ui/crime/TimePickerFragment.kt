package com.thewyp.android.criminalintent.ui.crime

import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment() {

    interface Callbacks {
        fun onTimeSelected(date: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val date = arguments?.getSerializable(ARG_DATE) as Date
        val calender = Calendar.getInstance()
        calender.time = date
        val listener = OnTimeSetListener() { _, hourOfDay, minute ->
            targetFragment?.let {
                val callbacks = it as Callbacks
                val resultDate = GregorianCalendar(
                    calender.get(Calendar.YEAR),
                    calender.get(Calendar.MONTH),
                    calender.get(Calendar.DAY_OF_MONTH),
                    hourOfDay,
                    minute).time
                callbacks.onTimeSelected(resultDate)
            }
        }
        return TimePickerDialog(
            requireActivity(),
            listener,
            calender.get(Calendar.HOUR_OF_DAY),
            calender.get(Calendar.MINUTE),
            true)
    }

    companion object {
        private const val ARG_DATE = "time"
        fun newInstance(date: Date): TimePickerFragment {
            return TimePickerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DATE, date)
                }
            }
        }
    }
}