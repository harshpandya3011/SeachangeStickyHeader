package com.seachange.healthandsafty.adapter

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seachange.healthandsafty.R
import com.seachange.healthandsafty.model.LeaderBoard
import com.seachange.healthandsafty.utills.setProgressAndAnimate
import kotlinx.android.synthetic.main.item_leaderboard.view.*
import kotlin.math.roundToInt

class LeaderboardRecyclerAdapter(items: List<LeaderBoard>?, private val isSiteLeaderBoard: Boolean = false) : RecyclerView.Adapter<LeaderboardRecyclerAdapter.LeaderboardViewHolder>() {
    var items = items
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, itemViewType: Int): LeaderboardViewHolder {
        return LeaderboardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_leaderboard, parent, false))
    }

    override fun getItemCount() = items?.count() ?: 0

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(items?.get(position), isSiteLeaderBoard)
    }

    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: LeaderBoard?, isSiteLeaderBoard: Boolean) {
            itemView.tv_leaderboard_name.text = item?.name
            itemView.tv_leaderboard_number.text = adapterPosition.inc().toString()
            val score = item?.score?.roundToInt()
            if (score == null) {
                itemView.tv_leaderboard_compliance_value.setText(R.string.dash)
            } else {
                itemView.tv_leaderboard_compliance_value.text = itemView.tv_leaderboard_compliance_value.context.getString(R.string.percent_value, score)
            }

            val statusColor = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, when (item?.statusRange) {
                LeaderBoard.AMBER -> R.color.orange
                LeaderBoard.GREEN -> R.color.colorDefaultGreen
                else -> R.color.red
            }))
            val statusColorTransparent = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, when (item?.statusRange) {
                LeaderBoard.AMBER -> R.color.orangeTransparent
                LeaderBoard.GREEN -> R.color.colorDefaultGreenTransparent
                else -> R.color.redTransparent
            }))

            itemView.progress_leaderboard_check_compliance_bottom.progressTintList = statusColor
            itemView.progress_leaderboard_check_compliance_background.progressTintList = statusColorTransparent

            itemView.progress_leaderboard_check_compliance_bottom.setProgressAndAnimate(score ?: 0)
            itemView.progress_leaderboard_check_compliance_background.setProgressAndAnimate(score
                    ?: 0)

            itemView.tv_leaderboard_tour_compliance_hazards.setText(if (isSiteLeaderBoard) R.string.tour_hazards else R.string.tour_compliance)
            val checkCompliance = item?.checkCompliance?.roundToInt()
            if (checkCompliance == null) {
                itemView.tv_leaderboard_check_compliance_value.setText(R.string.dash)
            } else {
                itemView.tv_leaderboard_check_compliance_value.text = itemView.tv_leaderboard_check_compliance_value.context.getString(R.string.percent_value, checkCompliance)
            }

            if (isSiteLeaderBoard) {
                val tourHazard = item?.tourHazard
                if (tourHazard == null) {
                    itemView.tv_leaderboard_tour_compliance_hazards_value.setText(R.string.dash)
                } else {
                    itemView.tv_leaderboard_tour_compliance_hazards_value.text = tourHazard.toString()
                }
            } else {
                val tourCompliance = item?.tourCompliance?.roundToInt()
                if (tourCompliance == null) {
                    itemView.tv_leaderboard_tour_compliance_hazards_value.setText(R.string.dash)
                } else {
                    itemView.tv_leaderboard_tour_compliance_hazards_value.text =
                            itemView.tv_leaderboard_tour_compliance_hazards_value.context.getString(
                                    R.string.percent_value,
                                    tourCompliance
                            )
                }
            }
        }
    }
}