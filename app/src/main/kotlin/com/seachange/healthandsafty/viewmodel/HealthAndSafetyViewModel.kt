package com.seachange.healthandsafty.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.seachange.healthandsafty.model.healthandsafety.HealthAndSafetyListData
import com.seachange.healthandsafty.offline.model.ModelConstants

class HealthAndSafetyViewModel(application: Application) : AndroidViewModel(application) {

    private val _healthAndSafetyListLiveData = MutableLiveData<ArrayList<HealthAndSafetyListData>>()
    val healthAndSafetyListLiveData: LiveData<ArrayList<HealthAndSafetyListData>> = _healthAndSafetyListLiveData
    private val _overAllProgress = MutableLiveData<Pair<Float, Int>>()
    val overAllProgress: LiveData<Pair<Float, Int>> = _overAllProgress

    companion object {
        enum class SectionList {
            GENERAL,
            STORAGE,
            EQUIPMENT_SAFETY,
            CAR_PARKING,
            CHEMICAL_SAFETY,
            WALFARE_FACILITY,
            ELECTRICAL,
            HOUSEKEEPING,
            FIRE_SAFETY,
            MANNUAL_HANDLING,
            SAFE_BEHAVIOUR,
            MANAGING_CORRECTIVES,
            TRAINING_STATUS,
            RA_REQUIRED,
            INCIDENT_ACCIDENT_STATUS
        }
    }

    fun getSections(): Int {
        return SectionList.values().size
    }

    fun fetchList() {
        val healthAndSafetyList = ArrayList<HealthAndSafetyListData>()
        //=====GENERAL STORE==========
        healthAndSafetyList.add(HealthAndSafetyListData(1, "GENERAL STORE", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.GENERAL, true, 1, 10))
        healthAndSafetyList.add(HealthAndSafetyListData(2, "Door closers defective, doors slamming closed", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.GENERAL, false, 1))
        healthAndSafetyList.add(HealthAndSafetyListData(3, "Fixed matting/ floor mats checked & OK", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.GENERAL, false, 1))
        healthAndSafetyList.add(HealthAndSafetyListData(4, "Steps/ ramps/ handrails checked, clutter free & OK", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.GENERAL, false, 1))
        healthAndSafetyList.add(HealthAndSafetyListData(5, "Safety signage is visible & in good condition", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.GENERAL, false, 1))
        healthAndSafetyList.add(HealthAndSafetyListData(6, "Area in front of the counters is clear & accessible", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.GENERAL, false, 1))
        healthAndSafetyList.add(HealthAndSafetyListData(7, "Broken or damaged floor surfaces e.g. carpet, tiles", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.GENERAL, false, 1))
        healthAndSafetyList.add(HealthAndSafetyListData(8, "Broken glass (e.g. mirrored surfaces, windows)", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.GENERAL, false, 1))
        healthAndSafetyList.add(HealthAndSafetyListData(9, "Exposed sharp edges (shelves, cabinets, furniture)", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.GENERAL, false, 1))
        healthAndSafetyList.add(HealthAndSafetyListData(10, "Mirrors/ pictures/ screens/signs fixed in place & secure", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.GENERAL, false, 1))
        healthAndSafetyList.add(HealthAndSafetyListData(11, "Furniture & furnishings checked & in good condition", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.GENERAL, false, 1))

        //=====STORE & RANKING==========
        healthAndSafetyList.add(HealthAndSafetyListData(12, "STORAGE & RACKING", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.STORAGE, true, 12, 7))
        healthAndSafetyList.add(HealthAndSafetyListData(13, "Racks/ shelves secure, stable & fit for purpose", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.STORAGE, false, 12))
        healthAndSafetyList.add(HealthAndSafetyListData(14, "Storage, shelving & racking inspected regularly", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.STORAGE, false, 12))
        healthAndSafetyList.add(HealthAndSafetyListData(15, "Displays are secure, stable and safe", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.STORAGE, false, 12))
        healthAndSafetyList.add(HealthAndSafetyListData(16, "Displays are clean, free of spillages/ debris", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.STORAGE, false, 12))
        healthAndSafetyList.add(HealthAndSafetyListData(17, "Goods stacked at a safe height & easily accessed", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.STORAGE, false, 12))
        healthAndSafetyList.add(HealthAndSafetyListData(18, "Stores have been subject to deep clean in last month", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.STORAGE, false, 12))
        healthAndSafetyList.add(HealthAndSafetyListData(19, "Access in & around storage areas kept clear, clean & tidy", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.STORAGE, false, 12))


        //=====EQUIPMENT SAFETY==========
        healthAndSafetyList.add(HealthAndSafetyListData(20, "EQUIPMENT SAFETY", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, true, 20, 14))
        healthAndSafetyList.add(HealthAndSafetyListData(21, "Equipment is used by authorised Staff only", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(22, "All equipment is serviced, visually checked & OK", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(23, "Staff isolate equipment before cleaning- switches in place", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(24, "SOPs in place for safe use of equipment/ machinery", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(25, "Canteen equipment is visually checked & OK", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(26, "Office equipment is visually checked & OK", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(27, "Roller shutters are in good working order", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(28, "Training up to date for use of Compactor/ Baler/ Bin Press", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(29, "Compactor/ Baler/ Bin Press checked, serviced and OK", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(30, "Stepladders/ steps designed for commercial use", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(31, "Stepladders/ steps have been checked & OK", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(32, "Safety knives/ box cutters available where required", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(33, "Machinery guarding in place & OK (where applicable)", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))
        healthAndSafetyList.add(HealthAndSafetyListData(34, "Fridges/ Freezers: condensation/ ice buid up (where applicable)", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.EQUIPMENT_SAFETY, false, 20))


        //=======CAR PARK & GOODS INWARD=======
        healthAndSafetyList.add(HealthAndSafetyListData(35, "CAR PARK & GOODS INWARDS", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CAR_PARKING, true, 35, 8))
        healthAndSafetyList.add(HealthAndSafetyListData(36, "Surface markings/ access routes are clearly visible", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CAR_PARKING, false, 35))
        healthAndSafetyList.add(HealthAndSafetyListData(37, "Safety signage is visible & in good condition", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CAR_PARKING, false, 35))
        healthAndSafetyList.add(HealthAndSafetyListData(38, "Gates & locks are in good condition & effective", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CAR_PARKING, false, 35))
        healthAndSafetyList.add(HealthAndSafetyListData(39, "Yard area checked & is unobstructed, clean, safe", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CAR_PARKING, false, 35))
        healthAndSafetyList.add(HealthAndSafetyListData(40, "Trolleys parked in designated bays/ areas", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CAR_PARKING, false, 35))
        healthAndSafetyList.add(HealthAndSafetyListData(41, "Loading/ Unloading area restricted, clear and tidy", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CAR_PARKING, false, 35))
        healthAndSafetyList.add(HealthAndSafetyListData(42, "Lifting machinery e.g. forklift, trolley, pallet truck checked & OK", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CAR_PARKING, false, 35))
        healthAndSafetyList.add(HealthAndSafetyListData(43, "PPE provided & worn by Staff in relevant External Areas", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CAR_PARKING, false, 35))

        //=======CHEMICAL SAFETY============
        healthAndSafetyList.add(HealthAndSafetyListData(44, "CHEMICAL SAFETY", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CHEMICAL_SAFETY, true, 44, 4))
        healthAndSafetyList.add(HealthAndSafetyListData(45, "Staff given information on chemicals they use", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CHEMICAL_SAFETY, false, 44))
        healthAndSafetyList.add(HealthAndSafetyListData(46, "SDS on file, up to date, stored in location of use", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CHEMICAL_SAFETY, false, 44))
        healthAndSafetyList.add(HealthAndSafetyListData(47, "Cleaning chemicals are stored away safely & secure", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CHEMICAL_SAFETY, false, 44))
        healthAndSafetyList.add(HealthAndSafetyListData(48, "Personal protective equipment (PPE) available", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.CHEMICAL_SAFETY, false, 44))


        //=======WELFARE FACILITIES===========
        healthAndSafetyList.add(HealthAndSafetyListData(49, "WELFARE FACILITIES", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.WALFARE_FACILITY, true, 49, 5))
        healthAndSafetyList.add(HealthAndSafetyListData(50, "Suitable canteen facilities in place, clean, tidy", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.WALFARE_FACILITY, false, 49))
        healthAndSafetyList.add(HealthAndSafetyListData(51, "Hot surfaces highlighted e.g. toaster/ boiler/ kettle", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.WALFARE_FACILITY, false, 49))
        healthAndSafetyList.add(HealthAndSafetyListData(52, "Cleaning checklist in place, up to date & signed", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.WALFARE_FACILITY, false, 49))
        healthAndSafetyList.add(HealthAndSafetyListData(53, "Toilet/ sink area clean & free from spillages/ leaks", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.WALFARE_FACILITY, false, 49))
        healthAndSafetyList.add(HealthAndSafetyListData(54, "Suitable controls to deal with threat/ aggression", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.WALFARE_FACILITY, false, 49))

        healthAndSafetyList.add(HealthAndSafetyListData(55, "ELECTRICAL", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.ELECTRICAL, true, 55, 4))
        healthAndSafetyList.add(HealthAndSafetyListData(56, "Leads in use visually checked & OK", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.ELECTRICAL, false, 55))
        healthAndSafetyList.add(HealthAndSafetyListData(57, "Sockets in use visually checked & OK", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.ELECTRICAL, false, 55))
        healthAndSafetyList.add(HealthAndSafetyListData(58, "Extension lead use minimised & not overloaded", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.ELECTRICAL, false, 55))
        healthAndSafetyList.add(HealthAndSafetyListData(59, "Toilet/ sink area clean & free from spillages/ leaks", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.ELECTRICAL, false, 55))

        healthAndSafetyList.add(HealthAndSafetyListData(60, "HOUSEKEEPING", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, true, 60, 15))
        healthAndSafetyList.add(HealthAndSafetyListData(61, "All cables routed/ tied down safely, no trailing leads", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(62, "Suitable resources available for cleaning", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(63, "System for cleaning spillages/ leaks immediately", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(64, "Floor surfaces swept/ vacuumed/ mopped", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(65, "Wet floor signs available, used appropriately", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(66, "Waste Management area checked & in good order", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(67, "Rubbish/ recycling is removed frequently", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(68, "Loose items/ bags/ baskets/ trolleys throughout store", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(69, "Cleaning checklists/ records up to date", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(70, "Aisles, stairs & working areas clear & clutter free", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(71, "Clear route available for deliveries", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(72, "Broken or damaged light fixtures/ covers/ bulbs", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(73, "Lighting levels are provided & maintained throughout", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(74, "Area behind counters kept clear & free of clutter", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))
        healthAndSafetyList.add(HealthAndSafetyListData(75, "Gas cylinders, where in use, stored securely & safely", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.HOUSEKEEPING, false, 60))

        healthAndSafetyList.add(HealthAndSafetyListData(76, "EMERGENCY RESPONSE/ FIRE SAFETY", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, true, 76, 13))
        healthAndSafetyList.add(HealthAndSafetyListData(77, "First aid kits fully stocked, accessible & highlighted", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))
        healthAndSafetyList.add(HealthAndSafetyListData(78, "First aid kit checks completed & OK", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))
        healthAndSafetyList.add(HealthAndSafetyListData(79, "Staff are aware of evacuation procedures e.g. drills", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))
        healthAndSafetyList.add(HealthAndSafetyListData(80, "Emergency contact details are displayed e.g. Gas", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))
        healthAndSafetyList.add(HealthAndSafetyListData(81, "Visitor sign in book in place & in use", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))
        healthAndSafetyList.add(HealthAndSafetyListData(82, "Emergency doors identified/ signage working", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))
        healthAndSafetyList.add(HealthAndSafetyListData(83, "Emergency exits unobstructed, open out, unlocked", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))
        healthAndSafetyList.add(HealthAndSafetyListData(84, "Fire alarm panel accessible, serviced & OK", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))
        healthAndSafetyList.add(HealthAndSafetyListData(85, "Fire Fighting equipment in place & serviced", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))
        healthAndSafetyList.add(HealthAndSafetyListData(86, "Emergency lighting in place, working & serviced", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))
        healthAndSafetyList.add(HealthAndSafetyListData(87, "Break Glass Units accessible & in working order", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))
        healthAndSafetyList.add(HealthAndSafetyListData(88, "Emergency controls in place for disabled people", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))
        healthAndSafetyList.add(HealthAndSafetyListData(89, "Sources of ignition & combustibles segregated/ separated", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.FIRE_SAFETY, false, 76))

        healthAndSafetyList.add(HealthAndSafetyListData(90, "MANUAL HANDLING/ ERGONOMICS", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.MANNUAL_HANDLING, true, 90, 6))
        healthAndSafetyList.add(HealthAndSafetyListData(91, "Workstations are ergonomically suitable for users", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.MANNUAL_HANDLING, false, 90))
        healthAndSafetyList.add(HealthAndSafetyListData(92, "Lifting aids available e.g. hand trolley, pallet truck", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.MANNUAL_HANDLING, false, 90))
        healthAndSafetyList.add(HealthAndSafetyListData(93, "Lifting aids visually checked & in good order, suitable for task", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.MANNUAL_HANDLING, false, 90))
        healthAndSafetyList.add(HealthAndSafetyListData(94, "Deliveries can be moved to storage/ shelving safely", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.MANNUAL_HANDLING, false, 90))
        healthAndSafetyList.add(HealthAndSafetyListData(95, "Staff can access goods at suitable height, without excessive", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.MANNUAL_HANDLING, false, 90))
        healthAndSafetyList.add(HealthAndSafetyListData(96, "Footstool/ step ladder available to avoid overreach", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.MANNUAL_HANDLING, false, 90))

        healthAndSafetyList.add(HealthAndSafetyListData(97, "SAFE BEHAVIOURS OBSERVED", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.SAFE_BEHAVIOUR, true, 97, 5))
        healthAndSafetyList.add(HealthAndSafetyListData(98, "Employees use stepladders/ steps for access", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.SAFE_BEHAVIOUR, false, 97))
        healthAndSafetyList.add(HealthAndSafetyListData(99, "Employees use the correct lifting techniques", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.SAFE_BEHAVIOUR, false, 97))
        healthAndSafetyList.add(HealthAndSafetyListData(100, "Employees wear appropriate protection (PPE)", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.SAFE_BEHAVIOUR, false, 97))
        healthAndSafetyList.add(HealthAndSafetyListData(101, "Employees know how to raise the alarm for help", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.SAFE_BEHAVIOUR, false, 97))
        healthAndSafetyList.add(HealthAndSafetyListData(102, "Employees show awareness of S/T/F hazards", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.SAFE_BEHAVIOUR, false, 97))

        healthAndSafetyList.add(HealthAndSafetyListData(103, "MANAGING CORRECTIVES", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.MANAGING_CORRECTIVES, true, 103, 4))
        healthAndSafetyList.add(HealthAndSafetyListData(104, "New Entries written up on CSA Board", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.MANAGING_CORRECTIVES, false, 103))
        healthAndSafetyList.add(HealthAndSafetyListData(105, "Entries are being written up correctly & fully", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.MANAGING_CORRECTIVES, false, 103))
        healthAndSafetyList.add(HealthAndSafetyListData(106, "Entries on the board for more than 3 months", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.MANAGING_CORRECTIVES, false, 103))
        healthAndSafetyList.add(HealthAndSafetyListData(107, "Correctives completed in the last week", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.MANAGING_CORRECTIVES, false, 103))

        healthAndSafetyList.add(HealthAndSafetyListData(108, "TRAINING STATUS", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.TRAINING_STATUS, true, 108, 3))
        healthAndSafetyList.add(HealthAndSafetyListData(109, "Staff trained in Manual Handling", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.TRAINING_STATUS, false, 108))
        healthAndSafetyList.add(HealthAndSafetyListData(110, "Staff trained in First Aid/ Fire Safety/ Warden", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.TRAINING_STATUS, false, 108))
        healthAndSafetyList.add(HealthAndSafetyListData(111, "Staff trained in Other (specify)", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.TRAINING_STATUS, false, 108))

        healthAndSafetyList.add(HealthAndSafetyListData(112, "RISK ASSESSMENTS (RA) REQUIRED", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.RA_REQUIRED, true, 112, 2))
        healthAndSafetyList.add(HealthAndSafetyListData(113, "Person specific (e.g. Pregnancy, MH, young person)?", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.RA_REQUIRED, false, 112))
        healthAndSafetyList.add(HealthAndSafetyListData(114, "New area, equipment, process, revamp?", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.RA_REQUIRED, false, 112))

        healthAndSafetyList.add(HealthAndSafetyListData(115, "INCIDENTS/ ACCIDENTS STATUS", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.INCIDENT_ACCIDENT_STATUS, true, 115, 1))
        healthAndSafetyList.add(HealthAndSafetyListData(116, "Accidents/ Incidents since last week", 0f, false, ModelConstants.NOT_APPLICABLE, SectionList.INCIDENT_ACCIDENT_STATUS, false, 115))

        _healthAndSafetyListLiveData.postValue(healthAndSafetyList)
        getOverAllProgress()
    }


    fun getOverAllProgress() {
        val healthSafetyList = healthAndSafetyListLiveData.value.orEmpty()

        var totalQuest = 0
        var checkedQuest = 0

        /*healthSafetyList.map {
            it.questionList.map { itQuest ->
                totalQuest++
                if (itQuest.status != ModelConstants.NOT_APPLICABLE)
                    checkedQuest++
            }
        }*/

        healthSafetyList.map {
            if (it.isHeader != true) {
                totalQuest++
                if (it.status != ModelConstants.NOT_APPLICABLE) {
                    checkedQuest++
                }
            }
        }

        val progressInPercentage = (100f * checkedQuest) / totalQuest
        _overAllProgress.postValue(Pair(progressInPercentage, checkedQuest))
    }

    fun updateList(healthList: java.util.ArrayList<HealthAndSafetyListData>) {
        _healthAndSafetyListLiveData.postValue(healthList)
    }


}