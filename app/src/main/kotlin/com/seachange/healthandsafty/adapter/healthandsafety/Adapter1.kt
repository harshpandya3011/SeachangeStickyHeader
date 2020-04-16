//package com.seachange.healthandsafty.adapter.healthandsafety
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.seachange.healthandsafty.R
//import com.seachange.healthandsafty.model.healthandsafety.HealthAndSafetyListData
//import com.seachange.healthandsafty.offline.model.ModelConstants
//import kotlinx.android.synthetic.main.item_health_category.view.*
//
//class Adapter1
//    : ListAdapter<HealthAndSafetyListData, Adapter1.Adapter1ViewHolder>(USERS_DIFF_CALLBACK) {
//
//
//    companion object {
//        const val ARG_PROGRESS = "ARG_PROGRESS"
//        val USERS_DIFF_CALLBACK = object : DiffUtil.ItemCallback<HealthAndSafetyListData>() {
//            override fun areItemsTheSame(oldItem: HealthAndSafetyListData, newItem: HealthAndSafetyListData) = oldItem.id == newItem.id
//
//            override fun areContentsTheSame(oldItem: HealthAndSafetyListData, newItem: HealthAndSafetyListData): Boolean {
//                return oldItem.progress == newItem.progress && oldItem.id == newItem.id && oldItem.title == newItem.title
//            }
//
//            override fun getChangePayload(oldItem: HealthAndSafetyListData, newItem: HealthAndSafetyListData): Any? {
//                val diffBundle = Bundle()
//                if (oldItem.progress != newItem.progress) {
//                    diffBundle.putFloat(ARG_PROGRESS, newItem.progress ?: 0f)
//                }
//                return if (diffBundle.isEmpty) null else diffBundle
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter1ViewHolder {
//        return Adapter1ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_health_category, parent, false))
//    }
//
//    override fun onBindViewHolder(holder: Adapter1ViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//
//    override fun onBindViewHolder(holder: Adapter1ViewHolder, position: Int, payloads: MutableList<Any>) {
//        val bundle = payloads.firstOrNull()?.takeIf { it is Bundle && !it.isEmpty } as Bundle?
//
//        if (bundle != null) {
//            holder.bind(getItem(position), bundle)
//        } else {
//            super.onBindViewHolder(holder, position, payloads)
//        }
//    }
//
//    private fun onClickParent(clickedView: View, item: HealthAndSafetyListData?, adapterPosition: Int) {
//        //TODO open child
//
//    }
//
//    class Adapter1ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        fun bind(item: HealthAndSafetyListData?) {
//            itemView.txtCategoryName.text = item?.title
////            healthAndSafetyList[position].childAdapter = childListAdapter
//            val checkedQuest = countCheckedQuestion(item)
//            itemView.txtProgressCategoryText.text = "$checkedQuest / ${item?.questionList?.size
//                    ?: 0}"
//            itemView.progressCategory.max = item?.questionList?.size ?: 0
//            itemView.progressCategory.progress = checkedQuest
//
//
//            val adapter2 = HealthChildAdapter(item?.questionList.orEmpty(), object : HealthChildAdapter.OnClickChild {
//                override fun onClickChild(view: View, parentPos: Int, childPos: Int) {
//                    if (item == null)
//                        return
//                    when (view.id) {
//                        R.id.btnYes -> {
//                            if (item.questionList[childPos].status == ModelConstants.YES) {
//                                item.questionList[childPos].status = ModelConstants.NOT_APPLICABLE
//                            } else {
//                                item.questionList[childPos].status = ModelConstants.YES
//                            }
//                        }
//                        R.id.btnNo -> {
//                            if (item.questionList[childPos].status == ModelConstants.NO) {
//                                item.questionList[childPos].status = ModelConstants.NOT_APPLICABLE
//                            } else {
//                                item.questionList[childPos].status = ModelConstants.NO
//                            }
//                        }
//                    }
//                }
//
//            }, adapterPosition)
//
//            itemView.rvChildList.adapter = adapter2
//
//            itemView.setOnClickListener {
//                if (itemView.rvChildList.visibility == View.VISIBLE) {
//                    itemView.rvChildList.visibility = View.GONE
//                } else {
//                    itemView.rvChildList.visibility = View.VISIBLE
//                }
//            }
//
//        }
//
//        fun bind(item: HealthAndSafetyListData?, bundle: Bundle) {
//            if (bundle.containsKey(ARG_PROGRESS)) {
//                itemView.txtProgressCategoryText.text = bundle.getString(ARG_PROGRESS)
//                val checkedQuest = countCheckedQuestion(item)
//                itemView.progressCategory.max = item?.questionList?.size ?: 0
//                itemView.progressCategory.progress = checkedQuest
//
//                itemView.setOnClickListener {
//                    if (itemView.rvChildList.visibility == View.VISIBLE) {
//                        itemView.rvChildList.visibility = View.GONE
//                    } else {
//                        itemView.rvChildList.visibility = View.VISIBLE
//                    }
//                }
//            }
//        }
//
//        private fun countCheckedQuestion(item: HealthAndSafetyListData?): Int {
//            var checkedQuest = 0
//            item?.questionList?.forEach {
//                if (it.status != ModelConstants.NOT_APPLICABLE) {
//                    checkedQuest++
//                }
//            }
//            return checkedQuest
//        }
//    }
//
//
//}