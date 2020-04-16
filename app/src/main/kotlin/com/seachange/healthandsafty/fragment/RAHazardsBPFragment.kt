package com.seachange.healthandsafty.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.RAHazardsBPActivity
import com.seachange.healthandsafty.adapter.RAHazardBPRecyclerViewAdapter
import com.seachange.healthandsafty.helper.Logger

import com.seachange.healthandsafty.model.HazardBP
import com.seachange.healthandsafty.model.RAHazards
import com.seachange.healthandsafty.presenter.RAHazardsPresenter
import com.seachange.healthandsafty.view.RAHazardView
import org.json.JSONObject


class RAHazardsBPFragment : BaseFragment(), RAHazardView {

    override fun receivedResponse(result: ArrayList<RAHazards>) {
        mHazardBPs = result
        updateListView(mHazardBPs)
    }

    override fun errorReceived(result: VolleyError) {

    }

    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    private var mHazardBPs: ArrayList<RAHazards> = ArrayList()
    private var mAdapter: RAHazardBPRecyclerViewAdapter? = null
    private lateinit var mRecyclerView: RecyclerView

    private var mPresenter: RAHazardsPresenter ?= null

    private var TYPE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        mPresenter = RAHazardsPresenter(this, mCtx)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list5, container, false)

        mRecyclerView = view.findViewById<View>(R.id.ra_hazard_bp_list) as RecyclerView
        mRecyclerView. layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        if (TYPE == 2) {
            mPresenter!!.fetchDataFromServer()
//        }
    }

    override fun onResume() {
        super.onResume()

//        when (TYPE) {
//            1->{
//                mHazardBPs = (activity as RAHazardsBPActivity).mHazardBPs
//            }
//            2->{
//                mHazardBPs = (activity as RAHazardsBPActivity).mHazards
//            }
//            3->{
//                mHazardBPs = (activity as RAHazardsBPActivity).mBPs
//            }
//        }
//        updateListView(mHazardBPs)
    }

    fun setPageType(mType: Int) {
        TYPE = mType
    }

    fun updateListView(mValues: ArrayList<RAHazards>) {
        mHazardBPs = mValues

        if (TYPE == 2){
            mAdapter = RAHazardBPRecyclerViewAdapter(mCtx, mHazardBPs, listener, true)
        } else {
            mAdapter = RAHazardBPRecyclerViewAdapter(mCtx, mHazardBPs, listener, false)
        }
        mRecyclerView.adapter = mAdapter
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onStop() {
        super.onStop()
    }
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: RAHazards?)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
                RAHazardsBPFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
