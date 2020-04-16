package com.seachange.healthandsafty.adapter.healthandsafety

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.model.healthandsafety.HealthAndSafetyListData
import com.seachange.healthandsafty.model.healthandsafety.ItemType
import com.seachange.healthandsafty.offline.model.ModelConstants
import com.seachange.healthandsafty.view.stickyheader.StickyHeaderItemDecoration
import com.seachange.healthandsafty.viewmodel.HealthAndSafetyViewModel
import kotlinx.android.synthetic.main.item_health_category.view.*
import org.jetbrains.anko.find


class HealthSafetyStickyAdapter internal constructor() : ListAdapter<HealthAndSafetyListData, RecyclerView.ViewHolder>(ModelDiffUtilCallback), StickyHeaderItemDecoration.HeaderEventListener {

    private lateinit var onHeaderInvalidate: OnHeaderInvalidate

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View
        return when (viewType) {
            ItemType.Header -> {
                itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_health_category, viewGroup, false)
                HeaderViewHolder(itemView)
            }
            ItemType.Post -> {
                itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_health_question, viewGroup, false)
                PostViewHolder(itemView)
            }
            else -> {
                itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_health_child_empty_layout, viewGroup, false)
                BlankViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int) {
        if (getItemViewType(i) == ItemType.Header) {
            (viewHolder as HeaderViewHolder).bind(i)
        } else {
            if (isItemExpand(i))
                (viewHolder as PostViewHolder).bind(i)
            else
                (viewHolder as BlankViewHolder).bind(i)
        }
    }

    internal inner class HeaderViewHolder(iView: View) : RecyclerView.ViewHolder(iView) {
        private val txtCategoryName: TextView = itemView.findViewById(R.id.txtCategoryName)
        private val txtCategorySubTitle: TextView = itemView.findViewById(R.id.txtCategorySubTitle)
        private val txtProgressCategoryText: TextView = itemView.findViewById(R.id.txtProgressCategoryText)
        private val progressCategory: ProgressBar = itemView.findViewById(R.id.progressCategory)
        private val relMainLayout: LinearLayout = itemView.findViewById(R.id.linMainLayout)
        private val imgArrow: ImageView = itemView.findViewById(R.id.imgArrow)


        init {
            txtProgressCategoryText.setOnClickListener { Log.e("tag", "Title click") }
            txtCategorySubTitle.setOnClickListener { Log.e("tag", "txtCategorySubTitle click") }
        }

        fun bind(headerPosition: Int) {
            val parentObj = getItem(headerPosition ?: 0)
            val sec = HealthAndSafetyViewModel.Companion.SectionList.valueOf(parentObj.sectionType.toString()).ordinal
            txtCategoryName.text = " " + (sec + 1) + " " + parentObj.title

            val checkedQuest = countCheckedQuestion(getItem(headerPosition))
            txtCategorySubTitle.text = "     $checkedQuest Checks"
            txtProgressCategoryText.text = "$checkedQuest / ${parentObj.childCount}"
            progressCategory.max = parentObj.childCount ?: 0
            progressCategory.progress = checkedQuest

            if (parentObj.isExpand == true) {
                imgArrow.rotation = 270f
            } else {
                imgArrow.rotation = 90f
            }

            relMainLayout.setOnClickListener {
                if (getItem(headerPosition).isExpand == true) {
                    imgArrow.animate().setDuration(200).rotation(90f)
                    onclickExpandCollapse(headerPosition, getItem(headerPosition))
                } else {
                    imgArrow.animate().setDuration(200).rotation(270f)
                    onclickExpandCollapse(headerPosition, getItem(headerPosition))
                }
            }

            itemView.setOnClickListener {
                if (getItem(headerPosition).isExpand == true) {
                    imgArrow.animate().setDuration(200).rotation(90f)
                    onclickExpandCollapse(headerPosition, getItem(headerPosition))
                } else {
                    imgArrow.animate().setDuration(200).rotation(270f)
                    onclickExpandCollapse(headerPosition, getItem(headerPosition))
                }
            }

        }
    }

    private fun onclickExpandCollapse(headerPosition: Int, item: HealthAndSafetyListData?) {
        if (item?.isExpand == true) {
            getItem(headerPosition).isExpand = false
            for (i in 1..item.childCount!!) {
                getItem(headerPosition + i).isExpand = false
                notifyItemChanged(headerPosition + i)
            }
        } else {
            getItem(headerPosition).isExpand = true
            for (i in 1..item?.childCount!!) {
                getItem(headerPosition + i).isExpand = true
                notifyItemChanged(headerPosition + i)
            }
        }
        notifyItemChanged(headerPosition)
    }

    internal inner class BlankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int) {
        }
    }


    internal inner class PostViewHolder(iView: View) : RecyclerView.ViewHolder(iView) {
        private val relChildLayout: RelativeLayout = itemView.findViewById(R.id.relChildMainLayout)
        private val txtQuestionName: TextView = itemView.findViewById(R.id.txtQuestionName)
        private val btnYes: TextView = itemView.findViewById(R.id.btnYes)
        private val btnNo: TextView = itemView.findViewById(R.id.btnNo)


        fun bind(position: Int) {
            val model = getItem(position)
            relChildLayout.visibility = View.VISIBLE
            relChildLayout.isClickable = false
            txtQuestionName.text = model.title
            when {
                model.status == ModelConstants.YES -> {
                    btnYes.isSelected = true
                    btnNo.isSelected = false
                }
                model.status == ModelConstants.NO -> {
                    btnYes.isSelected = false
                    btnNo.isSelected = true
                }
                else -> {
                    btnYes.isSelected = false
                    btnNo.isSelected = false
                }
            }
            btnYes.setOnClickListener {
                onClickChild(it, position)
            }
            btnNo.setOnClickListener {
                onClickChild(it, position)
            }
        }
    }

    private fun onClickChild(view: View?, position: Int) {
        when (view?.id) {
            R.id.btnYes -> {
                if (getItem(position).status == ModelConstants.YES) {
                    getItem(position).status = ModelConstants.NOT_APPLICABLE
                } else {
                    getItem(position).status = ModelConstants.YES
                }
            }
            R.id.btnNo -> {
                if (getItem(position).status == ModelConstants.NO) {
                    getItem(position).status = ModelConstants.NOT_APPLICABLE
                } else {
                    getItem(position).status = ModelConstants.NO
                }
            }
        }
        notifyItemChanged(position)
        notifyItemChanged((getItem(position).parentId ?: 0) - 1)
        onHeaderInvalidate.onHeaderValidate()
    }

    private fun isItemExpand(position: Int): Boolean {
        return getItem(position).isExpand ?: false
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).isHeader) {
            true -> ItemType.Header
            else -> when (getItem(position).isExpand) {
                true -> ItemType.Post
                else -> ItemType.Blank
            }
        }
    }

//    override fun getHeaderPositionForItem(itemPosition: Int?): Int? {
//        var headerPosition: Int? = 0
//        for (i in itemPosition!! downTo 1) {
//            if (isHeader(i)!!) {
//                headerPosition = i
//                return headerPosition
//            }
//        }
//        return headerPosition
//    }

//    override fun getHeaderLayout(headerPosition: Int?): Int? {
////        return R.layout.item_health_category
//    }

//    override fun bindHeaderData(header: View, headerPosition: Int) {
//        val txtCategoryName: TextView = header.txtCategoryName
//        val txtCategorySubTitle: TextView = header.txtCategorySubTitle
//        val txtProgressCategoryText: TextView = header.txtProgressCategoryText
//        val progressCategory: ProgressBar = header.progressCategory
//        val relMainLayout: RelativeLayout = header.relMainLayout
//        val imgArrow: ImageView = header.imgArrow
//
//        val parentObj = getItem(headerPosition ?: 0)
//        val sec = HealthAndSafetyViewModel.Companion.SectionList.valueOf(parentObj.sectionType.toString()).ordinal
//        txtCategoryName.text = " " + (sec + 1) + " " + parentObj.title
//        val checkedQuest = countCheckedQuestion(getItem(headerPosition))
//        txtCategorySubTitle.text = "     $checkedQuest Checks"
//        txtProgressCategoryText.text = "$checkedQuest / ${parentObj.childCount}"
//        progressCategory.max = parentObj.childCount ?: 0
//        progressCategory.progress = checkedQuest
//
//        if (parentObj.isExpand == true) {
//            imgArrow.rotation = 270f
//        } else {
//            imgArrow.rotation = 90f
//        }
//
//        relMainLayout.setOnClickListener {
//            if (getItem(headerPosition).isExpand == true) {
//                imgArrow.animate().setDuration(200).rotation(90f)
//                onclickExpandCollapse(headerPosition, getItem(headerPosition))
//            } else {
//                imgArrow.animate().setDuration(200).rotation(270f)
//                onclickExpandCollapse(headerPosition, getItem(headerPosition))
//            }
//        }
//
//        header.setOnClickListener {
//            if (getItem(headerPosition).isExpand == true) {
//                imgArrow.animate().setDuration(200).rotation(90f)
//                onclickExpandCollapse(headerPosition, getItem(headerPosition))
//            } else {
//                imgArrow.animate().setDuration(200).rotation(270f)
//                onclickExpandCollapse(headerPosition, getItem(headerPosition))
//            }
//        }
//
//    }

//    override fun isHeader(itemPosition: Int?): Boolean? {
//        return getItem(itemPosition!!).isHeader
//    }

    private fun countCheckedQuestion(item: HealthAndSafetyListData?): Int {
        var checkedQuest = 0
        var pos = item?.parentId ?: 0
        for (i in 0 until (item?.childCount ?: 1 - 1)) {
            if (getItem(pos).status != ModelConstants.NOT_APPLICABLE) {
                checkedQuest++
            }
            pos++
        }

        return checkedQuest
    }

    companion object {
        private val ModelDiffUtilCallback = object : DiffUtil.ItemCallback<HealthAndSafetyListData>() {
            override fun areItemsTheSame(model: HealthAndSafetyListData, t1: HealthAndSafetyListData): Boolean {
                return (model.status == t1.status || model.isExpand == t1.isExpand)
            }

            override fun areContentsTheSame(model: HealthAndSafetyListData, t1: HealthAndSafetyListData): Boolean {
                return model == t1
            }
        }
    }

    fun setOnHeaderInvalidate(onHeaderInvalidate: OnHeaderInvalidate) {
        this.onHeaderInvalidate = onHeaderInvalidate
    }

    interface OnHeaderInvalidate {
        fun onHeaderValidate()
    }

    override fun onHeaderClicked(view: View, headerPosition: Int) {
        if (getItem(headerPosition).isExpand == true) {
            view.imgArrow.animate().setDuration(200).rotation(90f)
            onclickExpandCollapse(headerPosition, getItem(headerPosition))
        } else {
            view.imgArrow.animate().setDuration(200).rotation(270f)
            onclickExpandCollapse(headerPosition, getItem(headerPosition))
        }
    }
//
//    override fun getHeaderPositionForItem(itemPosition: Int): Int {
//        var headerPosition: Int? = 0
//        for (i in itemPosition!! downTo 1) {
//            if (isHeader(i)!!) {
//                headerPosition = i
//                return headerPosition
//            }
//        }
//        return headerPosition!!
//    }
//
//    override fun getHeaderLayout(headerPosition: Int): Int {
//        return R.layout.item_health_category
//    }
//
//    override fun bindHeaderData(header: View?, headerPosition: Int) {
//        val header = header!!;
//        val txtCategoryName: TextView = header.txtCategoryName
//        val txtCategorySubTitle: TextView = header.txtCategorySubTitle
//        val txtProgressCategoryText: TextView = header.txtProgressCategoryText
//        val progressCategory: ProgressBar = header.progressCategory
//        val relMainLayout: RelativeLayout = header.relMainLayout
//        val imgArrow: ImageView = header.imgArrow
//
//        val parentObj = getItem(headerPosition ?: 0)
//        val sec = HealthAndSafetyViewModel.Companion.SectionList.valueOf(parentObj.sectionType.toString()).ordinal
//        txtCategoryName.text = " " + (sec + 1) + " " + parentObj.title
//        val checkedQuest = countCheckedQuestion(getItem(headerPosition))
//        txtCategorySubTitle.text = "     $checkedQuest Checks"
//        txtProgressCategoryText.text = "$checkedQuest / ${parentObj.childCount}"
//        progressCategory.max = parentObj.childCount ?: 0
//        progressCategory.progress = checkedQuest
//
//        if (parentObj.isExpand == true) {
//            imgArrow.rotation = 270f
//        } else {
//            imgArrow.rotation = 90f
//        }
//
//        relMainLayout.setOnClickListener {
//            if (getItem(headerPosition).isExpand == true) {
//                imgArrow.animate().setDuration(200).rotation(90f)
//                onclickExpandCollapse(headerPosition, getItem(headerPosition))
//            } else {
//                imgArrow.animate().setDuration(200).rotation(270f)
//                onclickExpandCollapse(headerPosition, getItem(headerPosition))
//            }
//        }
//
//        header?.setOnClickListener {
//            if (getItem(headerPosition).isExpand == true) {
//                imgArrow.animate().setDuration(200).rotation(90f)
//                onclickExpandCollapse(headerPosition, getItem(headerPosition))
//            } else {
//                imgArrow.animate().setDuration(200).rotation(270f)
//                onclickExpandCollapse(headerPosition, getItem(headerPosition))
//            }
//        }
//    }
//
//    override fun isHeader(itemPosition: Int): Boolean {
//        return getItem(itemPosition).isHeader!!
//    }
//
//    override fun onItemClicked(view: View, headerPosition: Int) {
//        val myHeaderPosition = getHeaderPositionForItem(headerPosition)
//        if (getItem(myHeaderPosition).isExpand == true) {
//            view.imgArrow.animate().setDuration(200).rotation(90f)
//            onclickExpandCollapse(myHeaderPosition, getItem(myHeaderPosition))
//        } else {
//            view.imgArrow.animate().setDuration(200).rotation(270f)
//            onclickExpandCollapse(myHeaderPosition, getItem(myHeaderPosition))
//        }
//    }
}
