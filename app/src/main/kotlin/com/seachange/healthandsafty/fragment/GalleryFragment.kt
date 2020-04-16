package com.seachange.healthandsafty.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.activity.RASelectOptionActivity
import com.seachange.healthandsafty.adapter.GalleryRecyclerViewAdapter
import com.seachange.healthandsafty.application.SCApplication
import com.seachange.healthandsafty.helper.HazardObserver

import com.seachange.healthandsafty.helper.PreferenceHelper
import com.seachange.healthandsafty.model.GalleryImage
import com.seachange.healthandsafty.presenter.GalleryPresenter
import com.seachange.healthandsafty.view.GalleryView
import kotlinx.android.synthetic.main.fragment_item_list6.*
import java.util.*

class GalleryFragment : BaseFragment(), GalleryView, Observer {

    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var mRecyclerView: RecyclerView
    private var mImageList: ArrayList<GalleryImage> = ArrayList()
    private var mAdapter: GalleryRecyclerViewAdapter? = null
    private var mPresenter: GalleryPresenter? = null
    private var mImageSelected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HazardObserver.getInstance().isImagesChanged = false
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

        mImageList = PreferenceHelper.getInstance(activity!!.applicationContext).savedImages
        listener = object : OnListFragmentInteractionListener {
            override fun onListFragmentInteraction(item: GalleryImage?) {
                mPresenter!!.galleryImageTapped(mImageList, item!!)
            }
        }
        mPresenter = GalleryPresenter(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        camera_continue_gallery.text = this.resources.getString(R.string.fa_camera)
        camera_continue_gallery.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.sysBlack))
        camera_Image_title_gallery.visibility = View.GONE

        camera_continue_gallery.typeface = SCApplication.FontMaterial()
        gallery_nav_back.typeface = SCApplication.FontMaterial()


        gallery_nav_back.setOnClickListener {
            activity!!.finish()

        }

        camera_continue_gallery.setOnClickListener {
            if (mImageSelected > 0) {
                PreferenceHelper.getInstance(activity!!.applicationContext).saveImageTakenData(mImageList)
                val intent = Intent(activity, RASelectOptionActivity::class.java)
                startActivity(intent)
            } else {
                activity!!.finish()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list6, container, false)
        val context = view.context

        mRecyclerView = view.findViewById<View>(R.id.image_list) as RecyclerView
        mRecyclerView.layoutManager = GridLayoutManager(context, columnCount) as RecyclerView.LayoutManager?
        mAdapter = GalleryRecyclerViewAdapter(mImageList, listener)
        mRecyclerView.adapter = mAdapter
        return view
    }

    override fun imageArrayUpdated(result: ArrayList<GalleryImage>) {
        mImageList = result
        mAdapter!!.updateImages(mImageList)
        mAdapter!!.notifyDataSetChanged()
    }

    override fun imagesCounter(result: Int) {
        mImageSelected = result
        if (camera_Image_title_gallery !=null) {
            camera_Image_title_gallery.text = result.toString()
            if (result > 0) {
                camera_continue_gallery.text = " " + this.resources.getString(R.string.fa_mail_send)
                camera_continue_gallery.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.sysBlue))
                camera_Image_title_gallery.visibility = View.VISIBLE
            } else {
                camera_continue_gallery.text = this.resources.getString(R.string.fa_camera)
                camera_continue_gallery.setTextColor(ContextCompat.getColor(activity!!.applicationContext, R.color.sysBlack))
                camera_Image_title_gallery.visibility = View.GONE
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onResume() {
        super.onResume()
        HazardObserver.getInstance().addObserver(this)
    }

    override fun update(p0: Observable?, p1: Any?) {

        if (p0 is HazardObserver) {
            val observer = p0 as HazardObserver
            if (observer.isImagesChanged) {
                mImageList = PreferenceHelper.getInstance(mCtx).savedImages
                imageArrayUpdated(mImageList)
                mPresenter!!.countImageSelected(mImageList)
                if (mImageList.size == 0) {
                    if (activity != null) {
                        activity!!.finish()
                    }
                }
            }
        }
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: GalleryImage?)
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"
        @JvmStatic
        fun newInstance(columnCount: Int) =
                GalleryFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_COLUMN_COUNT, columnCount)
                    }
                }
    }
}
