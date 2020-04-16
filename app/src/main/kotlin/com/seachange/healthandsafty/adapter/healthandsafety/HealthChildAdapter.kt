//package com.seachange.healthandsafty.adapter.healthandsafety
//
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.seachange.healthandsafty.R
//import com.seachange.healthandsafty.model.healthandsafety.HealthAndSafetyListData
//import com.seachange.healthandsafty.offline.model.ModelConstants
//import kotlinx.android.synthetic.main.item_health_question.view.*
//
//class HealthChildAdapter(
//        private val questionList: List<HealthAndSafetyListData.HealthAndSafetyQuestions>,
//        val onclickChild: OnClickChild,
//        val parentPosition: Int)
//    : RecyclerView.Adapter<HealthChildAdapter.ViewHolder>() {
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_health_question, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(childViewHolder: ViewHolder, position: Int) {
//        val childObj = questionList[position]
//        childViewHolder.txtQuestionName.text = childObj.title
//        if (childObj.status == ModelConstants.YES) {
//            Log.e("TAG", "====Child pos:$position parentPos: $parentPosition YES")
//            childViewHolder.btnYes.isSelected = true
//            childViewHolder.btnNo.isSelected = false
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                childViewHolder.btnYes.setTextColor(childViewHolder.btnYes.context.resources.getColor(R.color.sysWhite, null))
//////                childViewHolder.btnYes.setBackgroundColor(childViewHolder.btnYes.context.resources.getColor(R.color.colorPrimary, null))
//////                childViewHolder.btnNo.setBackgroundColor(childViewHolder.btnYes.context.resources.getColor(R.color.app_grey, null))
////                childViewHolder.btnNo.setTextColor(childViewHolder.btnNo.context.resources.getColor(R.color.sysBlack, null))
////            } else {
////                childViewHolder.btnYes.setTextColor(childViewHolder.btnYes.context.resources.getColor(R.color.sysWhite))
//////                childViewHolder.btnYes.setBackgroundColor(childViewHolder.btnYes.context.resources.getColor(R.color.app_grey))
//////                childViewHolder.btnNo.setBackgroundColor(childViewHolder.btnYes.context.resources.getColor(R.color.app_grey))
////                childViewHolder.btnNo.setTextColor(childViewHolder.btnNo.context.resources.getColor(R.color.sysBlack))
////            }
//        } else if (childObj.status == ModelConstants.NO) {
//            Log.e("TAG", "====Child pos:$position parentPos: $parentPosition NO")
//            childViewHolder.btnYes.isSelected = false
//            childViewHolder.btnNo.isSelected = true
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                childViewHolder.btnYes.setTextColor(childViewHolder.btnYes.context.resources.getColor(R.color.sysBlack, null))
//////                childViewHolder.btnYes.setBackgroundColor(childViewHolder.btnYes.context.resources.getColor(R.color.app_grey, null))
//////                childViewHolder.btnNo.setBackgroundColor(childViewHolder.btnYes.context.resources.getColor(R.color.colorPrimary, null))
////                childViewHolder.btnNo.setTextColor(childViewHolder.btnNo.context.resources.getColor(R.color.sysWhite, null))
////            } else {
////                childViewHolder.btnYes.setTextColor(childViewHolder.btnYes.context.resources.getColor(R.color.sysBlack))
////                childViewHolder.btnNo.setTextColor(childViewHolder.btnNo.context.resources.getColor(R.color.sysWhite))
//////                childViewHolder.btnYes.setBackgroundColor(childViewHolder.btnYes.context.resources.getColor(R.color.app_grey))
//////                childViewHolder.btnNo.setBackgroundColor(childViewHolder.btnYes.context.resources.getColor(R.color.app_grey))
////            }
//        } else {
//            Log.e("TAG", "====Child pos:$position parentPos: $parentPosition N/A")
//            childViewHolder.btnYes.isSelected = false
//            childViewHolder.btnNo.isSelected = false
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                childViewHolder.btnYes.setTextColor(childViewHolder.btnYes.context.resources.getColor(R.color.sysBlack, null))
////                childViewHolder.btnNo.setTextColor(childViewHolder.btnNo.context.resources.getColor(R.color.sysBlack, null))
////                childViewHolder.btnYes.setBackgroundColor(childViewHolder.btnYes.context.resources.getColor(R.color.app_grey))
////                childViewHolder.btnNo.setBackgroundColor(childViewHolder.btnYes.context.resources.getColor(R.color.app_grey))
////            } else {
////                childViewHolder.btnYes.setTextColor(childViewHolder.btnYes.context.resources.getColor(R.color.sysBlack))
////                childViewHolder.btnNo.setTextColor(childViewHolder.btnNo.context.resources.getColor(R.color.sysBlack))
////                childViewHolder.btnYes.setBackgroundColor(childViewHolder.btnYes.context.resources.getColor(R.color.app_grey))
////                childViewHolder.btnNo.setBackgroundColor(childViewHolder.btnYes.context.resources.getColor(R.color.app_grey))
////            }
//        }
//        childViewHolder.btnYes.setOnClickListener {
//            onclickChild.onClickChild(it, parentPosition, position)
//        }
//        childViewHolder.btnNo.setOnClickListener {
//            onclickChild.onClickChild(it, parentPosition, position)
//        }
//    }
//
//
//    override fun getItemCount(): Int = questionList.size
//
//    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
//        val txtQuestionName: TextView = mView.txtQuestionName
//        val btnYes: TextView = mView.btnYes
//        val btnNo: TextView = mView.btnNo
//    }
//
//    interface OnClickChild {
//        fun onClickChild(view: View, parentPos: Int, childPos: Int)
//    }
//}