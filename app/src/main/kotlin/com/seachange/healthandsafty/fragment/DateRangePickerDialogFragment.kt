package com.seachange.healthandsafty.fragment

import android.app.Dialog
import androidx.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.helper.RangeEndSelectionDecorator
import com.seachange.healthandsafty.helper.RangeExclusiveSelectionDecorator
import com.seachange.healthandsafty.helper.RangeSingleSelectionDecorator
import com.seachange.healthandsafty.helper.RangeStartSelectionDecorator
import com.seachange.healthandsafty.viewmodel.SiteGroupViewModel
import kotlinx.android.synthetic.main.dialog_picker_date_range.*
import org.threeten.bp.LocalDate

class DateRangePickerDialogFragment : DialogFragment(), DialogInterface.OnShowListener {

    companion object {
        const val TAG = "DateRangePickerDialogFragment"
        @JvmStatic
        fun newInstance(group: Boolean) =
                DateRangePickerDialogFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean(SiteGroupDashboardFragment.ARG_GROUP, group)
                    }
                }
    }

    private lateinit var siteGroupViewModel: SiteGroupViewModel
    private var group = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setView(R.layout.dialog_picker_date_range)
                .create().also {
                    it.setOnShowListener(this)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        group = arguments?.getBoolean(SiteGroupDashboardFragment.ARG_GROUP) ?: false
        siteGroupViewModel = ViewModelProviders.of(requireActivity())[SiteGroupViewModel::class.java]
    }

    override fun onShow(dialogInterface: DialogInterface?) {
        dialog?.material_calendar_view_date_range?.let {
            it.state().edit().setMaximumDate(CalendarDay.today()).commit()
            it.selectionMode = MaterialCalendarView.SELECTION_MODE_RANGE

            val doubleTileWidth = dialog?.window?.decorView?.width?.let { totalWidth ->
                totalWidth / 3.5f - resources.getDimension(R.dimen.date_range_picker_dialog_padding)
            }

            val selectedRange = (if (group) {
                siteGroupViewModel.selectedGroupDateRangeLiveData
            } else {
                siteGroupViewModel.selectedSiteDateRangeLiveData
            }).value
            if (selectedRange != null) {
                val colorPrimary = ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                if (selectedRange.first == selectedRange.second) {
                    it.addDecorator(RangeSingleSelectionDecorator(selectedRange.first, colorPrimary))
                } else {

                    it.addDecorator(RangeExclusiveSelectionDecorator(selectedRange, ContextCompat.getColor(requireContext(), R.color.yellowTransparent)))
                    it.addDecorator(RangeStartSelectionDecorator(
                            selectedRange,
                            colorPrimary,
                            ContextCompat.getColor(requireContext(), R.color.yellowTransparent),
                            doubleTileWidth
                    ))
                    it.addDecorator(RangeEndSelectionDecorator(
                            selectedRange,
                            colorPrimary,
                            ContextCompat.getColor(requireContext(), R.color.yellowTransparent),
                            doubleTileWidth
                    ))
                }

                it.setCurrentDate(selectedRange.first)
            }

            it.setOnDateChangedListener { widget, date, selected ->
                if (selected) widget.removeDecorators()
                if (!selected) {
                    selectDateRangeAndDismiss(date.date to date.date)
                }
            }

            it.setOnRangeSelectedListener { _, dates ->
                selectDateRangeAndDismiss(dates.first().date to dates.last().date)
            }
        }
    }

    private fun selectDateRangeAndDismiss(dateRange: Pair<LocalDate, LocalDate>) {
        if (group) {
            siteGroupViewModel.groupStatistics(dateRange)
        } else {
            siteGroupViewModel.siteStatistics(dateRange)
        }

        dismiss()
    }

}