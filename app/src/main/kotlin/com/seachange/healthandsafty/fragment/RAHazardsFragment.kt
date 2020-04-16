package com.seachange.healthandsafty.fragment

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.camera.CameraActivity
import com.seachange.healthandsafty.adapter.RAHazardsRecyclerViewAdapter
import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.model.ImageHazard
import com.seachange.healthandsafty.model.RiskCategory
import com.seachange.healthandsafty.presenter.RAHazardCategoryPresenter
import com.seachange.healthandsafty.utils.UtilStrings
import com.seachange.healthandsafty.view.HazardCategoryView
import kotlinx.android.synthetic.main.fragment_item_list4.*
import org.parceler.Parcels

class RAHazardsFragment : BaseFragment(), HazardCategoryView {

    private var columnCount = 1
    private var mHazardCategories: ArrayList<RiskCategory> = ArrayList()
    private var mAdapter: RAHazardsRecyclerViewAdapter? = null
    private lateinit var mRecyclerView: RecyclerView
    private var mHazardsSelected = 0
    private var selectedRisk: RiskCategory ?= null
    private var mPresenter:RAHazardCategoryPresenter ? =null
    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        mPresenter = RAHazardCategoryPresenter(this, activity!!.applicationContext)
        mPresenter!!.getRiskCategories()

        listener = object : OnListFragmentInteractionListener {

            override fun onListFragmentInteraction(item: RiskCategory?, position: Int) {

                selectedRisk = item
                if(mHazardCategories.get(position).selected){
                    mHazardCategories.get(position).selected = false
                    mHazardsSelected --
                } else {
                    mHazardCategories.get(position).selected = true
                    //reset selected
                    for (tmp in mHazardCategories) {
                        if (tmp != item && tmp.selected){
                            tmp.selected = false
                        }
                    }
                    mHazardsSelected =1
                }
                mAdapter!!.notifyDataSetChanged()
                updateHazards()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ra_hazard_continue.setOnClickListener {

            if (PreferenceHelper.getInstance(mCtx).raFlow == 1) {
                loadCameraScreen()
            } else {
                val mImageList = PreferenceHelper.getInstance(context).savedImages
                val arrayList: ArrayList<ImageHazard> = ArrayList()
                for(image in mImageList) {
                    if (image.selected) {
                        arrayList.add(ImageHazard(image.hazardId, image.timeFound, image.imageId, selectedRisk!!.id, selectedRisk!!.title))
                    }
                }
                mPresenter!!.postHazards(arrayList)
            }
        }
        updateHazards()
    }

    override fun catetoriesReceived(mCategories: ArrayList<RiskCategory>) {
        mHazardCategories = mCategories
        mAdapter = RAHazardsRecyclerViewAdapter(mCtx, mHazardCategories, listener)
        mRecyclerView.adapter = mAdapter
    }

    override fun postHazardSuccessfully() {
        activity!!.finish()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list4, container, false)
        val context = view.context
        mRecyclerView = view.findViewById<View>(R.id.ra_hazards_list) as RecyclerView
        mRecyclerView. layoutManager = LinearLayoutManager(context)
        return view
    }

    private fun loadCameraScreen() {
        val intent = Intent(activity, CameraActivity::class.java)
        intent.putExtra(UtilStrings.RISK_CATEGORY, Parcels.wrap<RiskCategory>(selectedRisk))
        startActivity(intent)
    }

    fun updateHazards() {
        ra_selected_hazards.text = Integer.toString(mHazardsSelected) + " Selected"
        if (mHazardsSelected>0) {
            ra_selected_hazards.visibility =  View.VISIBLE
            ra_hazard_continue.isEnabled = true
        } else {
            ra_selected_hazards.visibility = View.GONE
            ra_hazard_continue.isEnabled = false
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: RiskCategory?, position: Int)
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
                RAHazardsFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
