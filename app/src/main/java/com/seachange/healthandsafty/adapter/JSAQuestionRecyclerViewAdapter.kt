package com.seachange.healthandsafty.adapter

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.seachange.healthandsafty.R
import android.content.Context
import androidx.appcompat.widget.AppCompatRadioButton
import com.seachange.healthandsafty.model.JSAAnswer

import com.seachange.healthandsafty.fragment.JSAQuestionFragment.OnListFragmentInteractionListener

class JSAQuestionRecyclerViewAdapter(private val mCtx: Context, private var mValues: List<JSAAnswer>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<JSAQuestionRecyclerViewAdapter.ViewHolder>() {


    public fun reloadQuestion(mList: List<JSAAnswer>){
        this.mValues = mList
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item3, parent, false)
        return ViewHolder(view)
    }

    public override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues.get(position)
        holder.mContentView.text = mValues.get(position).answer
        holder.mContentView.text = mValues.get(position).answer

        if (holder.mItem!!.isChecked){
            holder.mView.setBackgroundColor(ContextCompat.getColor(mCtx, R.color.colorDefaultYellow))
            holder.mCheckButton.isChecked = true
            holder.mContentView.setTextColor(ContextCompat.getColor(mCtx, R.color.sysWhite))

        } else{
            holder.mView.setBackgroundResource(R.drawable.question_border)
            holder.mCheckButton.isChecked = false
            holder.mContentView.setTextColor(ContextCompat.getColor(mCtx, R.color.alertTitle))
        }
        val tmp = holder.mItem
        holder.mView.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                if (null != mListener) {
                    mListener.onAnswerSelectInteraction(tmp!!, position)
                }
            }
        })

        holder.mCheckButton.setOnClickListener {
            mListener?.onAnswerSelectInteraction(tmp!!, position)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        val mContentView: TextView = mView.findViewById<TextView>(R.id.content) as TextView
        val mCheckButton: AppCompatRadioButton = mView.findViewById<AppCompatRadioButton>(R.id.question_radio) as AppCompatRadioButton
        var mItem: JSAAnswer? = null
    }
}
