package com.seachange.healthandsafty.fragment.checklist_templates


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.seachange.healthandsafty.R
import kotlinx.android.synthetic.main.fragment_check_list_template.*

class CheckListTemplateFragment : Fragment() {

//    private lateinit var layoutManager: LinearLayoutManager
//    private lateinit var healthSafetyList: ArrayList<HealthAndSafetyListData>

//    private val healthAndSafetyViewModel by activityViewModels<HealthAndSafetyViewModel>()

//    private var healthAndSafetyListRecyclerAdapter: HealthParentAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_check_list_template, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(toolbarCheckListTemplate)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }


        fabCheckListTemplate.setOnClickListener {
            requireFragmentManager().commit {
                replace(R.id.fl_container_manage_user, CheckListTemplateTitleFragment())
                addToBackStack(null)
            }
        }

    }

}
