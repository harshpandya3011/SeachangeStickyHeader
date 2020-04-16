//package com.seachange.healthandsafty.adapter.healthandsafety
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.ProgressBar
//import android.widget.RelativeLayout
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.seachange.healthandsafty.R
//import com.seachange.healthandsafty.model.healthandsafety.HealthAndSafetyListData
//import com.seachange.healthandsafty.offline.model.ModelConstants
//import kotlinx.android.synthetic.main.item_health_category.view.*
//
//
//class HealthParentAdapter(
//        private val healthList: ArrayList<HealthAndSafetyListData>,
//        var onOverAllProgressChange: OnOverAllProgressChange,
//        var context: Context)
//    : RecyclerView.Adapter<HealthParentAdapter.ViewHolder>() {
//
//    private val onChildClick = object : HealthChildAdapter.OnClickChild {
//        override fun onClickChild(view: View, parentPos: Int, childPos: Int) {
//            if (parentPos > itemCount)
//                return
//            when (view.id) {
//                R.id.btnYes -> {
//                    if (healthList[parentPos].questionList[childPos].status == ModelConstants.YES) {
//                        healthList[parentPos].questionList[childPos].status = ModelConstants.NOT_APPLICABLE
//                    } else {
//                        healthList[parentPos].questionList[childPos].status = ModelConstants.YES
//                    }
//                }
//                R.id.btnNo -> {
//                    if (healthList[parentPos].questionList[childPos].status == ModelConstants.NO) {
//                        healthList[parentPos].questionList[childPos].status = ModelConstants.NOT_APPLICABLE
//                    } else {
//                        healthList[parentPos].questionList[childPos].status = ModelConstants.NO
//                    }
//                }
//            }
//            notifyItemChanged(parentPos)
//            onOverAllProgressChange.onOverAllProgressChange(healthList)
//        }
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.item_health_category, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
//        val parentObj = healthList[position]
//        viewHolder.txtCategoryName.text = " " + (position + 1) + " " + parentObj.title
//        val checkedQuest = countCheckedQuestion(healthList[position])
//        viewHolder.txtCategorySubTitle.text = "     $checkedQuest Checks"
//        viewHolder.txtProgressCategoryText.text = "$checkedQuest / ${parentObj.questionList.size}"
//        viewHolder.progressCategory.max = parentObj.questionList.size
//        viewHolder.progressCategory.progress = checkedQuest
//
//        if (healthList[position].isExpand == true) {
//            viewHolder.rvChildList.visibility = View.VISIBLE
//            viewHolder.imgArrow.rotation = 270f
//        } else {
//            viewHolder.rvChildList.visibility = View.GONE
//            viewHolder.imgArrow.rotation = 90f
//        }
//
//        val adapter2: HealthChildAdapter
//        if (healthList[position].childAdapter == null) {
//            adapter2 = HealthChildAdapter(parentObj.questionList, onChildClick, position)
//
//            healthList[position].childAdapter = adapter2
//            viewHolder.rvChildList.adapter = adapter2
//        } else
//            viewHolder.rvChildList.adapter = healthList[position].childAdapter
//        viewHolder.relMainLayout.setOnClickListener {
//            if (viewHolder.rvChildList.visibility == View.VISIBLE) {
//                viewHolder.imgArrow.animate().setDuration(200).rotation(90f)
//                healthList[position].isExpand = false
//                viewHolder.rvChildList.visibility = View.GONE
//            } else {
//                viewHolder.imgArrow.animate().setDuration(200).rotation(270f)
//                healthList[position].isExpand = true
//                viewHolder.rvChildList.visibility = View.VISIBLE
//            }
//        }
//    }
//
//    private fun countCheckedQuestion(item: HealthAndSafetyListData?): Int {
//        var checkedQuest = 0
//        item?.questionList?.forEach {
//            if (it.status != ModelConstants.NOT_APPLICABLE) {
//                checkedQuest++
//            }
//        }
//        return checkedQuest
//    }
//
//    override fun getItemCount(): Int = healthList.size
//
//    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
//        val txtCategoryName: TextView = mView.txtCategoryName
//        val txtCategorySubTitle: TextView = mView.txtCategorySubTitle
//        val txtProgressCategoryText: TextView = mView.txtProgressCategoryText
//        val progressCategory: ProgressBar = mView.progressCategory
//        val rvChildList: RecyclerView = mView.rvChildList
//        val relMainLayout: RelativeLayout = mView.relMainLayout
//        val imgArrow: ImageView = mView.imgArrow
//    }
//
//    interface OnOverAllProgressChange {
//        fun onOverAllProgressChange(healthList: ArrayList<HealthAndSafetyListData>)
//    }
//}