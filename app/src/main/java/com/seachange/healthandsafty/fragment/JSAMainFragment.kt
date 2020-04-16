package com.seachange.healthandsafty.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.fragment_item_list2.*
import com.seachange.healthandsafty.model.JSAModel

import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.adapter.JSARecyclerViewAdapter
import com.seachange.healthandsafty.fragment.dummy.DummyContent
import com.seachange.healthandsafty.helper.HazardObserver
import com.seachange.healthandsafty.helper.PreferenceHelper
import java.util.*


class JSAMainFragment : BaseFragment(), Observer {

    private var mColumnCount = 1
    private var mListener: OnListFragmentInteractionListener? = null
    private lateinit var mRecyclerView: RecyclerView
    private var jsaModel:JSAModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mColumnCount = arguments!!.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list2, container, false)
        val context = view.context

        mRecyclerView = view.findViewById<View>(R.id.jsaContentRecyclyerView) as RecyclerView
        mRecyclerView. layoutManager = LinearLayoutManager(context) as RecyclerView.LayoutManager?
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.isFocusable = false
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        jsaModel = PreferenceHelper.getInstance(mCtx).siteJsaData
        val screenWidth = mCtx.resources.displayMetrics.widthPixels
        val height = screenWidth * 3/4
        jsa_header_image.layoutParams.height = height
        loadData()
    }

    private fun loadData() {

        if (jsaModel == null){
            return
        }

        mRecyclerView.adapter = JSARecyclerViewAdapter(mCtx, jsaModel!!.jsaHazards, mListener)

        ImageLoader.getInstance().displayImage(jsaModel!!.primaryImage, jsa_header_image )
        assess_date.text = jsaModel!!.assessmentDate
        jsa_creator.text = "Created by ${jsaModel!!.creator}"
        jsa_name_address.text = jsaModel!!.name
        jsa_title.text = jsaModel!!.title
    }

    override fun onResume() {
        super.onResume()
        HazardObserver.getInstance().addObserver(this)

    }

    override fun update(p0: Observable?, p1: Any?) {
        val observer = p0 as HazardObserver
        if (observer.isJsaReceived) {
            jsaModel = PreferenceHelper.getInstance(mCtx).siteJsaData
            loadData()
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: DummyContent.DummyItem)
    }

    companion object {

        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int): JSAMainFragment {
            val fragment = JSAMainFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
