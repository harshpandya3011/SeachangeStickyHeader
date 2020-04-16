package com.seachange.healthandsafty.fragment


import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.NoConnectionError
import com.android.volley.ServerError
import com.android.volley.TimeoutError
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.adapter.LeaderboardRecyclerAdapter
import com.seachange.healthandsafty.network.model.response.GroupsStatisticsResponse
import com.seachange.healthandsafty.network.model.response.SitesStatisticsResponse
import com.seachange.healthandsafty.utills.DATE_FORMAT_EEE_dd_MMM_yy
import com.seachange.healthandsafty.utills.DATE_FORMAT_dd_MMM_yy
import com.seachange.healthandsafty.utills.setProgressAndAnimate
import com.seachange.healthandsafty.utils.Truss
import com.seachange.healthandsafty.viewmodel.SiteGroupViewModel
import kotlinx.android.synthetic.main.fragment_site_group_dashboard.*
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoField
import kotlin.math.roundToInt


class SiteGroupDashboardFragment : Fragment(), TabLayout.OnTabSelectedListener {

    companion object {
        const val ARG_GROUP = "ARG_GROUP"
        @JvmStatic
        fun newInstance(group: Boolean) =
                SiteGroupDashboardFragment().apply {
                    arguments = Bundle().apply {
                        putBoolean(ARG_GROUP, group)
                    }
                }
    }

    private lateinit var siteGroupViewModel: SiteGroupViewModel
    private lateinit var leaderboardRecyclerAdapter: LeaderboardRecyclerAdapter

    private var group = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        group = arguments?.getBoolean(ARG_GROUP) ?: false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_site_group_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModelAndObserver()

        tab_layout_site_dashboard.addOnTabSelectedListener(this)
        rv_leader_board.isNestedScrollingEnabled = false

        if (group) {
            leaderboardRecyclerAdapter = LeaderboardRecyclerAdapter(siteGroupViewModel.groupsLeaderBoardLiveData.value)
            tv_title_leader_board.setText(R.string.sites_leader_board)

            tab_layout_site_dashboard.getTabAt(siteGroupViewModel.selectedGroupTabPosition)?.takeUnless { it.isSelected }?.let {
                tab_layout_site_dashboard.setScrollPosition(it.position, 0f, true)
                it.select()
            }

            if (siteGroupViewModel.groupStatisticsResponseLiveData.value == null) {
                tv_site_group_no_data_error.visibility = View.GONE
                group_site_group_dashboard.visibility = View.GONE
            }
        } else {
            leaderboardRecyclerAdapter = LeaderboardRecyclerAdapter(siteGroupViewModel.sitesLeaderBoardLiveData.value, true)
            tv_title_leader_board.setText(R.string.zones_leader_board)

            tab_layout_site_dashboard.getTabAt(siteGroupViewModel.selectedSiteTabPosition)?.takeUnless { it.isSelected }?.let {
                tab_layout_site_dashboard.setScrollPosition(it.position, 0f, true)
                it.select()
            }

            if (siteGroupViewModel.siteStatisticsResponseLiveData.value == null) {
                tv_site_group_no_data_error.visibility = View.GONE
                group_site_group_dashboard.visibility = View.GONE
            }
            tv_number_of_sites.setText(R.string.one_site)
        }

        rv_leader_board.adapter = leaderboardRecyclerAdapter
    }

    private fun initViewModelAndObserver() {
        siteGroupViewModel = ViewModelProviders.of(requireActivity())[SiteGroupViewModel::class.java]
        if (group) {
            siteGroupViewModel.groupStatisticsResponseLiveData.observe(this, Observer(::bindGroupStatisticsResponse))
        } else {
            siteGroupViewModel.siteStatisticsResponseLiveData.observe(this, Observer(::bindSiteStatisticsResponse))
        }

        (if (group) siteGroupViewModel.selectedGroupDateRangeLiveData else siteGroupViewModel.selectedSiteDateRangeLiveData)
                .observe(this, Observer {
                    setTabTextAndSelection(it)
                })

        (if (group) siteGroupViewModel.groupsLeaderBoardLiveData else siteGroupViewModel.sitesLeaderBoardLiveData)
                .observe(this, Observer {
                    leaderboardRecyclerAdapter.items = it
                })

        (if (group) siteGroupViewModel.errorGroupLiveData else siteGroupViewModel.errorSiteLiveData)
                .observe(this, Observer {
                    tv_site_group_no_data_error.setText(R.string.site_dashboard_error)
                    tv_site_group_no_data_error.visibility = View.VISIBLE
                    group_site_group_dashboard.visibility = View.GONE
                    when (it) {
                        is NoConnectionError -> {
                            AlertDialog.Builder(requireContext())
                                    .setTitle(R.string.no_connection_error_title)
                                    .setMessage(R.string.no_connection_error)
                                    .setPositiveButton(R.string.close) { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .setIcon(R.drawable.ic_cloud_off_black_24dp)
                                    .show()
                            tv_site_group_no_data_error.setText(R.string.site_dashboard_error)
                            tv_site_group_no_data_error.visibility = View.VISIBLE
                            group_site_group_dashboard.visibility = View.GONE
                        }
                        is ServerError -> {
                            AlertDialog.Builder(requireContext())
                                    .setTitle(R.string.server_error_title)
                                    .setMessage(R.string.server_error)
                                    .setPositiveButton(R.string.close) { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .setIcon(R.drawable.ic_mood_bad_black_24dp)
                                    .show()
                            tv_site_group_no_data_error.setText(R.string.site_dashboard_error)
                            tv_site_group_no_data_error.visibility = View.VISIBLE
                            group_site_group_dashboard.visibility = View.GONE
                        }
                        is TimeoutError -> {
                            AlertDialog.Builder(requireContext())
                                    .setTitle(R.string.timeout_error_title)
                                    .setMessage(R.string.timeout_error)
                                    .setPositiveButton(R.string.close) { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .setIcon(R.drawable.ic_access_time_black_24dp)
                                    .show()
                            tv_site_group_no_data_error.setText(R.string.site_dashboard_error)
                            tv_site_group_no_data_error.visibility = View.VISIBLE
                            group_site_group_dashboard.visibility = View.GONE
                        }
                    }
                })
    }

    private fun bindSiteStatisticsResponse(siteStatistics: SitesStatisticsResponse?) {
        if (siteStatistics == null) {
            tv_site_group_no_data_error.setText(R.string.site_dashboard_no_data_available)
            tv_site_group_no_data_error.visibility = View.VISIBLE
            group_site_group_dashboard.visibility = View.GONE
            return
        }

        group_site_group_dashboard.visibility = View.VISIBLE
        tv_site_group_no_data_error.visibility = View.GONE

        val numCompleted = siteStatistics.siteTourSummary?.complianceSummary?.numCompleted
        if (numCompleted == null) {
            tv_scheduled_completed_tours_value.setText(R.string.dash)
        } else {
            val numScheduled = siteStatistics.siteTourSummary.complianceSummary.numScheduled
            tv_scheduled_completed_tours_value.text = Truss()
                    .append(getString(R.string.comma_separated_value, numCompleted))
                    .pushSpan(RelativeSizeSpan(0.58F))
                    .pushSpan(ForegroundColorSpan(
                            ContextCompat.getColor(
                                    requireContext(),
                                    com.google.android.material.R.color.secondary_text_default_material_light
                            )
                    ))
                    .append('/')
                    .append(
                            if (numScheduled == null) {
                                getString(R.string.dash)
                            } else {
                                getString(R.string.comma_separated_value, numScheduled)
                            }
                    )
                    .popSpan()
                    .popSpan()
                    .build()
        }


        val siteTourCompliance = siteStatistics.siteTourSummary?.complianceSummary?.compliance?.roundToInt()
        if (siteTourCompliance == null) {
            tv_tour_compliance_value.setText(R.string.dash)
        } else {
            tv_tour_compliance_value.text = getString(R.string.percent_value, siteTourCompliance)
        }
        progress_tour_compliance.setProgressAndAnimate(siteTourCompliance ?: 0)

        val numOfHazards = siteStatistics.siteTourSummary?.numOfHazards
        if (numOfHazards == null) {
            tv_tour_hazards_value.setText(R.string.dash)
        } else {
            tv_tour_hazards_value.text = getString(R.string.comma_separated_value, numOfHazards)
        }

        val numZoneCheckCompleted = siteStatistics.zoneCheckSummary?.complianceSummary?.numCompleted
        if (numZoneCheckCompleted == null) {
            tv_scheduled_completed_checks_value.setText(R.string.dash)
        } else {
            val numZoneCheckScheduled = siteStatistics.zoneCheckSummary.complianceSummary.numScheduled
            tv_scheduled_completed_checks_value.text = Truss()
                    .append(getString(R.string.comma_separated_value, numZoneCheckCompleted))
                    .pushSpan(RelativeSizeSpan(0.58F))
                    .pushSpan(ForegroundColorSpan(
                            ContextCompat.getColor(
                                    requireContext(),
                                    com.google.android.material.R.color.secondary_text_default_material_light
                            )
                    ))
                    .append('/')
                    .append(
                            if (numZoneCheckScheduled == null) {
                                getString(R.string.dash)
                            } else {
                                getString(R.string.comma_separated_value, numZoneCheckScheduled)
                            }
                    )
                    .popSpan()
                    .popSpan()
                    .build()
        }

        val zoneCheckCompliance = siteStatistics.zoneCheckSummary?.complianceSummary?.compliance?.roundToInt()
        if (zoneCheckCompliance == null) {
            tv_check_compliance_value.setText(R.string.dash)
        } else {
            tv_check_compliance_value.text = getString(R.string.percent_value, zoneCheckCompliance)
        }
        progress_check_compliance.setProgressAndAnimate(zoneCheckCompliance ?: 0)

        val numOfZoneCheckHazards = siteStatistics.zoneCheckSummary?.numOfHazards
        if (numOfZoneCheckHazards == null) {
            tv_check_hazards_value.setText(R.string.dash)
        } else {
            tv_check_hazards_value.text = getString(R.string.comma_separated_value, numOfZoneCheckHazards)
        }

        val zoneCount = siteStatistics.zones?.count()
        if (zoneCount == null) {
            tv_number_of_zones.setText(R.string.dash)
        } else {
            tv_number_of_zones.text = resources.getQuantityString(R.plurals.plural_zones, zoneCount, zoneCount)
        }
    }

    private fun bindGroupStatisticsResponse(groupStatistics: GroupsStatisticsResponse?) {
        if (groupStatistics == null) {
            tv_site_group_no_data_error.setText(R.string.site_dashboard_no_data_available)
            tv_site_group_no_data_error.visibility = View.VISIBLE
            group_site_group_dashboard.visibility = View.GONE
            return
        }

        group_site_group_dashboard.visibility = View.VISIBLE
        tv_site_group_no_data_error.visibility = View.GONE

        val numCompleted = groupStatistics.siteTourSummary?.complianceSummary?.numCompleted
        if (numCompleted == null) {
            tv_scheduled_completed_tours_value.setText(R.string.dash)
        } else {
            val numScheduled = groupStatistics.siteTourSummary.complianceSummary.numScheduled
            tv_scheduled_completed_tours_value.text = Truss()
                    .append(getString(R.string.comma_separated_value, numCompleted))
                    .pushSpan(RelativeSizeSpan(0.58F))
                    .pushSpan(ForegroundColorSpan(
                            ContextCompat.getColor(
                                    requireContext(),
                                    com.google.android.material.R.color.secondary_text_default_material_light
                            )
                    ))
                    .append('/')
                    .append(
                            if (numScheduled == null) {
                                getString(R.string.dash)
                            } else {
                                getString(R.string.comma_separated_value, numScheduled)
                            }
                    )
                    .popSpan()
                    .popSpan()
                    .build()
        }


        val siteTourCompliance = groupStatistics.siteTourSummary?.complianceSummary?.compliance?.roundToInt()
        if (siteTourCompliance == null) {
            tv_tour_compliance_value.setText(R.string.dash)
        } else {
            tv_tour_compliance_value.text = getString(R.string.percent_value, siteTourCompliance)
        }
        progress_tour_compliance.setProgressAndAnimate(siteTourCompliance ?: 0)

        val numOfHazards = groupStatistics.siteTourSummary?.numOfHazards
        if (numOfHazards == null) {
            tv_tour_hazards_value.setText(R.string.dash)
        } else {
            tv_tour_hazards_value.text = getString(R.string.comma_separated_value, numOfHazards)
        }

        val numZoneCheckCompleted = groupStatistics.zoneCheckSummary?.complianceSummary?.numCompleted
        if (numZoneCheckCompleted == null) {
            tv_scheduled_completed_checks_value.setText(R.string.dash)
        } else {
            val numZoneCheckScheduled = groupStatistics.zoneCheckSummary.complianceSummary.numScheduled
            tv_scheduled_completed_checks_value.text = Truss()
                    .append(getString(R.string.comma_separated_value, numZoneCheckCompleted))
                    .pushSpan(RelativeSizeSpan(0.58F))
                    .pushSpan(ForegroundColorSpan(
                            ContextCompat.getColor(
                                    requireContext(),
                                    com.google.android.material.R.color.secondary_text_default_material_light
                            )
                    ))
                    .append('/')
                    .append(
                            if (numZoneCheckScheduled == null) {
                                getString(R.string.dash)
                            } else {
                                getString(R.string.comma_separated_value, numZoneCheckScheduled)
                            }
                    )
                    .popSpan()
                    .popSpan()
                    .build()
        }

        val zoneCheckCompliance = groupStatistics.zoneCheckSummary?.complianceSummary?.compliance?.roundToInt()
        if (zoneCheckCompliance == null) {
            tv_check_compliance_value.setText(R.string.dash)
        } else {
            tv_check_compliance_value.text = getString(R.string.percent_value, zoneCheckCompliance)
        }
        progress_check_compliance.setProgressAndAnimate(zoneCheckCompliance ?: 0)

        val numOfZoneCheckHazards = groupStatistics.zoneCheckSummary?.numOfHazards
        if (numOfZoneCheckHazards == null) {
            tv_check_hazards_value.setText(R.string.dash)
        } else {
            tv_check_hazards_value.text = getString(R.string.comma_separated_value, numOfZoneCheckHazards)
        }

        val numOfZones = groupStatistics.numOfZones
        if (numOfZones == null) {
            tv_number_of_zones.setText(R.string.dash)
        } else {
            tv_number_of_zones.text = resources.getQuantityString(R.plurals.plural_zones, numOfZones, numOfZones)
        }

        val numOfSites = groupStatistics.numOfSites
        if (numOfSites == null) {
            tv_number_of_sites.setText(R.string.dash)
        } else {
            tv_number_of_sites.text = resources.getQuantityString(R.plurals.plural_sites, numOfSites, numOfSites)
        }
    }

    private fun setTabTextAndSelection(selectedDateRange: Pair<LocalDate, LocalDate>?) {
        if (selectedDateRange == null) return

        tab_layout_site_dashboard.getTabAt(SiteGroupViewModel.TAB_DATE_RANGE)?.text = if (selectedDateRange.first == selectedDateRange.second) {
            selectedDateRange.first.format(DateTimeFormatter.ofPattern(DATE_FORMAT_EEE_dd_MMM_yy))
        } else {
            DateTimeFormatter.ofPattern(DATE_FORMAT_dd_MMM_yy).let { formatter ->
                getString(R.string.two_value_with_dash_in_two_lines, selectedDateRange.first.format(formatter), selectedDateRange.second.format(formatter))
            }
        }
    }

    //region tab selection listener
    override fun onTabReselected(tab: TabLayout.Tab?) {
        if (tab?.position == SiteGroupViewModel.TAB_DATE_RANGE) {
            DateRangePickerDialogFragment.newInstance(group).show(childFragmentManager, DateRangePickerDialogFragment.TAG)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        //no-op
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {

        if (tab?.position == SiteGroupViewModel.TAB_DATE_RANGE) {
            if ((if (group) siteGroupViewModel.selectedGroupTabPosition else siteGroupViewModel.selectedSiteTabPosition) != tab.position) {
                DateRangePickerDialogFragment.newInstance(group).show(childFragmentManager, DateRangePickerDialogFragment.TAG)
            }
            if (group) {
                siteGroupViewModel.selectedGroupTabPosition = tab.position
            } else {
                siteGroupViewModel.selectedSiteTabPosition = tab.position
            }
            return
        }

        val now = LocalDate.now()
        val dateRange = when (tab?.position) {
            SiteGroupViewModel.TAB_TODAY -> now to now
            SiteGroupViewModel.TAB_WEEK -> now.with(ChronoField.DAY_OF_WEEK, 1) to now
            SiteGroupViewModel.TAB_MONTH -> now.withDayOfMonth(1) to now
            else -> now.withDayOfYear(1) to now
        }
        if (group) {
            tab?.position?.let {
                siteGroupViewModel.selectedGroupTabPosition = it
            }
            siteGroupViewModel.groupStatistics(dateRange)
        } else {
            tab?.position?.let {
                siteGroupViewModel.selectedSiteTabPosition = it
            }
            siteGroupViewModel.siteStatistics(dateRange)
        }
    }
    //endregion

    override fun onDestroyView() {
        super.onDestroyView()
        tab_layout_site_dashboard.clearOnTabSelectedListeners()
    }
}
