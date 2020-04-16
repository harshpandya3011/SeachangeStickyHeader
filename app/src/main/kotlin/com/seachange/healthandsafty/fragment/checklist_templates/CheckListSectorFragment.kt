package com.seachange.healthandsafty.fragment.checklist_templates


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.adapter.check_list_template.CheckListTemplateAdapter
import com.seachange.healthandsafty.model.check_list_type.CheckListTypeData
import com.seachange.healthandsafty.viewmodel.checklist_template.CheckListTemplateViewModel
import kotlinx.android.synthetic.main.fragment_check_list_type.*

class CheckListSectorFragment : Fragment() {

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var checkListTypeList: ArrayList<CheckListTypeData>

    private val checkListTemplateViewModel by activityViewModels<CheckListTemplateViewModel>()

    private var checkListTemplateAdapter: CheckListTemplateAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_check_list_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(toolbarCheckListType)
            supportActionBar?.title = "Checklist Sector"
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        checkListTemplateViewModel.fetchCheckSectorList()
        checkListTemplateViewModel.checkListSectorLiveData.observe(this, Observer {
            if (checkListTemplateAdapter == null) {
                attachAdapter()
            } else {

            }
        })

        btnProceed.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun attachAdapter() {
        layoutManager = LinearLayoutManager(requireContext())
        checkListTypeList = checkListTemplateViewModel.checkListSectorLiveData.value!!
        checkListTemplateAdapter = CheckListTemplateAdapter(checkListTypeList, onItemClick, requireContext())
        rvCheckListType.layoutManager = layoutManager
        rvCheckListType.adapter = checkListTemplateAdapter
        rvCheckListType.itemAnimator = null
        checkValidation()
    }

    private val onItemClick = object : CheckListTemplateAdapter.OnItemSelected {
        override fun onItemSelected(view: View, position: Int, checkListType: ArrayList<CheckListTypeData>) {
            for (i in 0 until checkListType.size) {
                checkListType[i].selected = i == position
            }

            checkListTemplateAdapter?.setCheckList(checkListType)
            checkListTemplateAdapter?.notifyDataSetChanged()
            checkValidation()
        }
    }

    private fun checkValidation() {
        var isAnySelected = false
        checkListTypeList.forEach {
            if (it.selected == true)
                isAnySelected = true
        }

        btnProceed.isEnabled = isAnySelected
    }


}
