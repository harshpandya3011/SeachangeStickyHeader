package com.seachange.healthandsafty.viewmodel.checklist_template

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.seachange.healthandsafty.model.check_list_type.CheckListTypeData

class CheckListTemplateViewModel(application: Application) : AndroidViewModel(application) {

    private val _checkListTypeLiveData = MutableLiveData<ArrayList<CheckListTypeData>>()
    val checkListTypeLiveData: LiveData<ArrayList<CheckListTypeData>> = _checkListTypeLiveData
    private val _checkListSectorLiveData = MutableLiveData<ArrayList<CheckListTypeData>>()
    val checkListSectorLiveData: LiveData<ArrayList<CheckListTypeData>> = _checkListSectorLiveData


    fun fetchCheckSectorList() {

        val checkListType = ArrayList<CheckListTypeData>()
        checkListType.add(CheckListTypeData("Grocery", 1, false))
        checkListType.add(CheckListTypeData("Fashion", 2, false))
        checkListType.add(CheckListTypeData("Leisure", 3, false))
        checkListType.add(CheckListTypeData("Hospitality", 4, false))
        checkListType.add(CheckListTypeData("Grocery", 5, false))
        checkListType.add(CheckListTypeData("Fashion", 6, false))
        checkListType.add(CheckListTypeData("Leisure", 7, false))
        checkListType.add(CheckListTypeData("Hospitality", 8, false))
        checkListType.add(CheckListTypeData("Grocery", 9, false))
        checkListType.add(CheckListTypeData("Fashion", 10, false))
        checkListType.add(CheckListTypeData("Leisure", 11, false))
        checkListType.add(CheckListTypeData("Hospitality", 12, false))
        checkListType.add(CheckListTypeData("Grocery", 13, false))
        checkListType.add(CheckListTypeData("Fashion", 14, false))
        checkListType.add(CheckListTypeData("Leisure", 15, false))
        checkListType.add(CheckListTypeData("Hospitality", 16, false))
        checkListType.add(CheckListTypeData("Grocery", 17, false))
        checkListType.add(CheckListTypeData("Fashion", 18, false))
        checkListType.add(CheckListTypeData("Leisure", 19, false))
        checkListType.add(CheckListTypeData("Hospitality", 20, false))
        checkListType.add(CheckListTypeData("Grocery", 21, false))

        _checkListSectorLiveData.postValue(checkListType)
    }

    fun fetchCheckTypeList() {

        val checkListType = ArrayList<CheckListTypeData>()
        checkListType.add(CheckListTypeData("Hygiene", 1, false))
        checkListType.add(CheckListTypeData("Vehicle", 2, false))
        checkListType.add(CheckListTypeData("General Housekeeping", 3, false))
        checkListType.add(CheckListTypeData("Hygiene", 4, false))
        checkListType.add(CheckListTypeData("Vehicle", 5, false))
        checkListType.add(CheckListTypeData("General Housekeeping", 6, false))
        checkListType.add(CheckListTypeData("Hygiene", 7, false))
        checkListType.add(CheckListTypeData("Vehicle", 8, false))
        checkListType.add(CheckListTypeData("General Housekeeping", 9, false))
        checkListType.add(CheckListTypeData("Hygiene", 10, false))
        checkListType.add(CheckListTypeData("Vehicle", 11, false))
        checkListType.add(CheckListTypeData("General Housekeeping", 12, false))
        checkListType.add(CheckListTypeData("Hygiene", 13, false))
        checkListType.add(CheckListTypeData("Vehicle", 14, false))
        checkListType.add(CheckListTypeData("General Housekeeping", 15, false))
        checkListType.add(CheckListTypeData("Hygiene", 16, false))
        checkListType.add(CheckListTypeData("Vehicle", 17, false))
        checkListType.add(CheckListTypeData("General Housekeeping", 18, false))
        checkListType.add(CheckListTypeData("Hygiene", 19, false))
        checkListType.add(CheckListTypeData("Vehicle", 20, false))
        checkListType.add(CheckListTypeData("General Housekeeping", 21, false))

        _checkListTypeLiveData.postValue(checkListType)
    }

}