package com.seachange.healthandsafty.fragment.healthandsafety


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.adapter.healthandsafety.HealthSafetyStickyAdapter
import com.seachange.healthandsafty.model.healthandsafety.HealthAndSafetyListData
import com.seachange.healthandsafty.model.healthandsafety.ItemType
import com.seachange.healthandsafty.view.stickyheader.StickyHeaderItemDecoration
import com.seachange.healthandsafty.viewmodel.HealthAndSafetyViewModel
import kotlinx.android.synthetic.main.fragment_health_safety.*


class HealthAndSafetyFragment : Fragment() {

    private val onHeaderInvalidate = object : HealthSafetyStickyAdapter.OnHeaderInvalidate {
        override fun onHeaderValidate() {
            healthAndSafetyViewModel.updateList(ArrayList<HealthAndSafetyListData>(healthAndSafetyListRecyclerAdapter?.currentList))
        }

    }
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var healthSafetyList: ArrayList<HealthAndSafetyListData>

    private val healthAndSafetyViewModel by activityViewModels<HealthAndSafetyViewModel>()

    private var healthAndSafetyListRecyclerAdapter: HealthSafetyStickyAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_health_safety, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).apply {
            setSupportActionBar(toolbar_health_safety)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        healthAndSafetyViewModel.healthAndSafetyListLiveData.observe(this, Observer {
            if (healthAndSafetyListRecyclerAdapter == null) {
                attachAdapter()
            } else {
                healthAndSafetyViewModel.getOverAllProgress()
            }
        })

        healthAndSafetyViewModel.overAllProgress.observe(this, Observer {
            progressOverAll.progress = it.first.toInt()
            txtProgressOverAll.text = "${it.first.toInt()}%"
            txtCategorySubTitle.text = "${healthAndSafetyViewModel.getSections()} Sections | ${it.second} Checks"
        })

    }

    private fun attachAdapter() {

        layoutManager = LinearLayoutManager(requireContext())
        healthSafetyList = healthAndSafetyViewModel.healthAndSafetyListLiveData.value!!
        healthAndSafetyListRecyclerAdapter = HealthSafetyStickyAdapter(/*healthSafetyList, onOverAllProgressChange, requireContext()*/)
        healthAndSafetyListRecyclerAdapter?.setOnHeaderInvalidate(onHeaderInvalidate)
        rv_health_safety.addItemDecoration(StickyHeaderItemDecoration(rv_health_safety, false, isHeader(), healthAndSafetyListRecyclerAdapter as StickyHeaderItemDecoration.HeaderEventListener))
        rv_health_safety.layoutManager = layoutManager
        rv_health_safety.adapter = healthAndSafetyListRecyclerAdapter
        rv_health_safety.itemAnimator = null
        healthAndSafetyListRecyclerAdapter?.submitList(healthSafetyList)
        rv_health_safety.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisibleItem = firstVisibleItemPosition + visibleItemCount

                Log.e("TAG", "visibleItemCount: $visibleItemCount totalItemCount: $totalItemCount firstVisibleItemCount: $firstVisibleItemPosition lastVisibleItem: $lastVisibleItem")
            }
        })
    }

    private fun isHeader(): (itemPosition: Int) -> Boolean {
        return {
            (rv_health_safety.adapter as HealthSafetyStickyAdapter).getItemViewType(it) == ItemType.Header
        }
    }

}
