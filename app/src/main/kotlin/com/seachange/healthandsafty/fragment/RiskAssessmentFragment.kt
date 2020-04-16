package com.seachange.healthandsafty.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.VolleyError
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.adapter.RiskAssessmentRecyclerViewAdapter

import com.seachange.healthandsafty.model.RiskAssement
import com.seachange.healthandsafty.presenter.RiskAssessPresenter
import com.seachange.healthandsafty.view.RiskAssessView

class RiskAssessmentFragment : BaseFragment(), RiskAssessView {

    private var index = 1
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var mRecyclerView: RecyclerView
    private var mAdapter: RiskAssessmentRecyclerViewAdapter? = null
    private var mPresenter: RiskAssessPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            index = it.getInt(ARG_INDEX)
        }

        if (index == 1) {
            //upcoming
        } else {
            //previous
        }

        mPresenter = RiskAssessPresenter(this, mCtx)
        mPresenter!!.getAssessments()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_risk_assessment_list, container, false)
        mRecyclerView = view.findViewById(R.id.raMainlist) as RecyclerView
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView. layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun receivedResponse(arrayList: ArrayList<RiskAssement>) {
        showData(arrayList)
    }

    override fun errorReceived(result: VolleyError) {

    }

    fun showData(arrayList:ArrayList<RiskAssement>) {
        mAdapter = RiskAssessmentRecyclerViewAdapter(arrayList, listener)
        mRecyclerView.adapter = mAdapter
    }
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
       fun onListFragmentInteraction(item: RiskAssement?)
    }

    companion object {

        const val ARG_INDEX = "index-count"

        @JvmStatic
        fun newInstance(indexCount: Int) =
                RiskAssessmentFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_INDEX, indexCount)
                    }
                }
    }
}
