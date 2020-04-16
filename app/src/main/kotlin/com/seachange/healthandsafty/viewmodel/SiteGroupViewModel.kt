package com.seachange.healthandsafty.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.seachange.healthandsafty.application.AppController
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.model.CaygoSite
import com.seachange.healthandsafty.model.LeaderBoard
import com.seachange.healthandsafty.network.model.response.GroupsStatisticsResponse
import com.seachange.healthandsafty.network.model.response.SitesStatisticsResponse
import com.seachange.healthandsafty.utils.SingleLiveEvent
import com.seachange.healthandsafty.utils.UtilStrings
import org.threeten.bp.LocalDate

class SiteGroupViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAB_DATE_RANGE = 0
        const val TAB_TODAY = 1
        const val TAB_WEEK = 2
        const val TAB_MONTH = 3
        const val TAB_YEAR = 4
    }

    private val caygoSite: CaygoSite?
    private val accessToken: String?

    var selectedSiteTabPosition: Int = TAB_TODAY
    var selectedGroupTabPosition: Int = TAB_TODAY

    init {
        val preferenceHelper = PreferenceHelper.getInstance(application)
        caygoSite = preferenceHelper.siteData
        accessToken = preferenceHelper.requestToken()
    }

    private val _selectedSiteDateRangeLiveData: MutableLiveData<Pair<LocalDate, LocalDate>?> = MutableLiveData()
    val selectedSiteDateRangeLiveData: LiveData<Pair<LocalDate, LocalDate>?> = _selectedSiteDateRangeLiveData

    private val _progressSiteLiveData: MutableLiveData<Boolean?> = MutableLiveData()
    val progressSiteLiveData: LiveData<Boolean?> = _progressSiteLiveData

    private val _errorSiteLiveData: MutableLiveData<VolleyError?> = SingleLiveEvent()
    val errorSiteLiveData: LiveData<VolleyError?> = _errorSiteLiveData

    private val _progressGroupLiveData: MutableLiveData<Boolean?> = MutableLiveData()
    val progressGroupLiveData: LiveData<Boolean?> = _progressGroupLiveData

    private val _errorGroupLiveData: MutableLiveData<VolleyError?> = SingleLiveEvent()
    val errorGroupLiveData: LiveData<VolleyError?> = _errorGroupLiveData

    private val _selectedGroupDateRangeLiveData: MutableLiveData<Pair<LocalDate, LocalDate>?> = MutableLiveData()
    val selectedGroupDateRangeLiveData: LiveData<Pair<LocalDate, LocalDate>?> = _selectedGroupDateRangeLiveData

    private val _siteStatisticsResponseLiveData: MutableLiveData<SitesStatisticsResponse?> = MutableLiveData()
    val siteStatisticsResponseLiveData: LiveData<SitesStatisticsResponse?> = _siteStatisticsResponseLiveData

    val sitesLeaderBoardLiveData: LiveData<List<LeaderBoard>?> = Transformations.map(siteStatisticsResponseLiveData) {
        it?.zones?.map { zone ->
            LeaderBoard(
                    zone.name,
                    zone.overallScore,
                    zone.ragStatus,
                    zone.complianceSummary?.compliance,
                    tourHazard = zone.numOfSiteTourHazardsIdentified
            )
        }
    }

    private val _groupStatisticsResponseLiveData: MutableLiveData<GroupsStatisticsResponse?> = MutableLiveData()
    val groupStatisticsResponseLiveData: LiveData<GroupsStatisticsResponse?> = _groupStatisticsResponseLiveData

    val groupsLeaderBoardLiveData: LiveData<List<LeaderBoard>?> = Transformations.map(groupStatisticsResponseLiveData) {
        it?.sites?.map { site ->
            LeaderBoard(
                    site.name,
                    site.overallScore,
                    site.ragStatus,
                    site.zoneCheckSummary?.complianceSummary?.compliance,
                    site.siteTourSummary?.complianceSummary?.compliance
            )
        }
    }

    private var siteStatisticsRequest: StringRequest? = null
    private var groupStatisticsRequest: StringRequest? = null

    fun siteStatistics(dateRange: Pair<LocalDate, LocalDate>, refresh: Boolean = false) {
        val oldDateRange = selectedSiteDateRangeLiveData.value
        _selectedSiteDateRangeLiveData.postValue(dateRange)
        if (caygoSite == null || accessToken == null || (!refresh && oldDateRange == dateRange)) return

        siteStatisticsRequest = object : StringRequest(
                "${UtilStrings.CAYGO_ROOT_API}/${caygoSite.site_id}/statistics?timeMin=${dateRange.first}&timeMax=${dateRange.second}",
                Response.Listener<String> {
                    _progressSiteLiveData.postValue(false)
                    _siteStatisticsResponseLiveData.postValue(Gson().fromJson(it, SitesStatisticsResponse::class.java))
                },
                Response.ErrorListener {
                    _progressSiteLiveData.postValue(false)
                    _errorSiteLiveData.postValue(it)
                }
        ) {
            override fun getHeaders() = super.getHeaders().toMutableMap().also {
                it["Authorization"] = accessToken
            }

        }.apply {
            _progressSiteLiveData.postValue(true)
            AppController.getInstance().addToRequestQueue(this)
        }
    }

    fun groupStatistics(dateRange: Pair<LocalDate, LocalDate>, refresh: Boolean = false) {
        val oldDateRange = selectedGroupDateRangeLiveData.value
        _selectedGroupDateRangeLiveData.postValue(dateRange)
        if (caygoSite == null || accessToken == null || (!refresh && oldDateRange == dateRange)) return

        groupStatisticsRequest = object : StringRequest(
                "${UtilStrings.CAYGO_ROOT_API}/groups/${caygoSite.groupId}/statistics?timeMin=${dateRange.first}&timeMax=${dateRange.second}",
                Response.Listener<String> {
                    _progressGroupLiveData.postValue(false)
                    _groupStatisticsResponseLiveData.postValue(Gson().fromJson(it, GroupsStatisticsResponse::class.java))
                },
                Response.ErrorListener {
                    _progressGroupLiveData.postValue(false)
                    _errorGroupLiveData.postValue(it)
                }
        ) {
            override fun getHeaders() = super.getHeaders().toMutableMap().also {
                it["Authorization"] = accessToken
            }

        }.apply {
            _progressGroupLiveData.postValue(true)
            AppController.getInstance().addToRequestQueue(this)
        }
    }

    fun refreshSiteStatistics() {
        selectedSiteDateRangeLiveData.value?.let {
            siteStatistics(it, true)
        }
    }

    fun refreshGroupStatistics() {
        selectedGroupDateRangeLiveData.value?.let {
            groupStatistics(it, true)
        }
    }

    override fun onCleared() {
        super.onCleared()
        siteStatisticsRequest?.cancel()
        groupStatisticsRequest?.cancel()
        siteStatisticsRequest = null
        groupStatisticsRequest = null
    }

}