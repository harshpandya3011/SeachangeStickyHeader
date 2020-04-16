package com.seachange.healthandsafty.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.seachange.healthandsafty.R

import com.seachange.healthandsafty.fragment.GalleryFragment.OnListFragmentInteractionListener
import com.seachange.healthandsafty.model.GalleryImage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_item6.view.*

class GalleryRecyclerViewAdapter(
        private var mValues: List<GalleryImage>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<GalleryRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as GalleryImage
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    fun updateImages(images:List<GalleryImage>) {
        mValues = images
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item6, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = position.toString()

        Picasso.get()
                .load("file://"+ item.path)
                .resize(250, 250)
                .centerCrop()
                .into(holder.mImage)

        if (item.selected) {
            holder.mIdView.setBackgroundResource(R.drawable.circle)
            if (item.position !=null) {
                holder.mIdView.text = item.position.toString()
            }
        } else {
            holder.mIdView.setBackgroundResource(R.drawable.circle_unselect)
            holder.mIdView.text = ""
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content
        val mImage: ImageView = mView.gallery_image_icon

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
