package com.seachange.healthandsafty.fragment.checklist_templates


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.seachange.healthandsafty.R
import kotlinx.android.synthetic.main.fragment_check_list_template.toolbarCheckListTemplate
import kotlinx.android.synthetic.main.fragment_check_list_template_title.*

class CheckListTemplateTitleFragment : Fragment() {

//    private lateinit var layoutManager: LinearLayoutManager
//    private lateinit var healthSafetyList: ArrayList<HealthAndSafetyListData>

//    private val healthAndSafetyViewModel by activityViewModels<HealthAndSafetyViewModel>()

//    private var healthAndSafetyListRecyclerAdapter: HealthParentAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_check_list_template_title, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(toolbarCheckListTemplate)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }


        edtTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkValidation()
            }

        })


        btnProceed.setOnClickListener {
            if (edtTitle.text.toString().contains("type", true)) {
                requireActivity().supportFragmentManager.commit {
                    replace(R.id.fl_container_manage_user, CheckListTypeFragment())
                    addToBackStack(null)
                }
            } else/* if (edtTitle.text.toString().contains("sector", true))*/ {
                requireActivity().supportFragmentManager.commit {
                    replace(R.id.fl_container_manage_user, CheckListSectorFragment())
                    addToBackStack(null)
                }
            }
        }

    }

    private fun checkValidation() {
        btnProceed.isEnabled = edtTitle.text.toString().trim().isNotEmpty()
    }

}
