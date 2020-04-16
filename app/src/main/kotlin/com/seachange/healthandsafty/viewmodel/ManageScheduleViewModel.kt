package com.seachange.healthandsafty.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.android.volley.VolleyError
import com.seachange.healthandsafty.utils.SingleLiveEvent

class ManageScheduleViewModel (application: Application) : AndroidViewModel(application) {

    private val _errorLiveData = SingleLiveEvent<VolleyError?>()
    val errorLiveData: LiveData<VolleyError?> = _errorLiveData

    fun postError(error: VolleyError?) {
        _errorLiveData.postValue(error)
    }
}