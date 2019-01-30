package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.dependencies.view.Dimension
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.VoteKind
import com.scruge.scruge.model.entity.VoteResult
import com.scruge.scruge.view.views.ButtonView
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import kotlinx.android.synthetic.main.cell_controls.view.*
import kotlinx.android.synthetic.main.cell_countdown.view.*
import kotlinx.android.synthetic.main.cell_vote_info.view.*
import kotlinx.android.synthetic.main.cell_vote_result.view.*
import java.util.*

class VoteResultCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(result:VoteResult?):VoteResultCell {
        if (result != null) {
            val onePercent = Math.max(0.01, result.voters.toDouble() / 100)
            val positivePercent = result.positiveVotes.toDouble() / onePercent
            val negativePercent = (result.voters - result.positiveVotes).toDouble() / onePercent

            itemView.result_no.text = negativePercent.formatRounding() + "%"
            itemView.result_yes.text = positivePercent.formatRounding() + "%"

            itemView.result_background.viewTreeObserver.addOnGlobalLayoutListener {
                val width = itemView.result_background.measuredWidth
                itemView.result_view.layoutParams.width = width * (positivePercent / 100).toInt()
            }
        }
        else {
            itemView.result_no.text = "…"
            itemView.result_yes.text = "…"
        }

        return this
    }
}

class VoteInfoCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm: CampaignVM, kind: VoteKind) {
        itemView.vote_info_title.text = vm.title
        itemView.vote_info_date.text = vm.daysLeft

        if (kind == VoteKind.extend) {
            itemView.vote_info_description.text = R.string.label_vote_extend_description.string()
        }
        else {
            itemView.vote_info_description.text = R.string.label_vote_release_description.string()
        }
    }
}

class VoteControlsCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(block:(String, Boolean)->Unit): VoteControlsCell {
        itemView.controls_yes.color = ButtonView.Color.green
        itemView.controls_yes.title = R.string.do_vote_for.string()
        itemView.controls_no.title = R.string.do_vote_against.string()

        itemView.controls_no.click { block(itemView.controls_password.text.toString(), false) }
        itemView.controls_yes.click { block(itemView.controls_password.text.toString(), true) }

        return this
    }
}

class CountdownCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var timestamp: Long = 0

    fun setup(title:String, timestamp:Long):CountdownCell {
        itemView.countdown_title.text = title
        this.timestamp = timestamp
        refresh()
        return this
    }

    private fun refresh() {
        val MINUTE = 1000 * 60
        val HOUR = 60 * MINUTE
        val DAY = 24 * HOUR

        val diff = Math.max(0, timestamp - Date().time)

        val days = diff / DAY
        val hours = (diff - days * DAY) / HOUR
        val minutes = (diff - days * DAY - hours * HOUR) / MINUTE

        itemView.countdown_days.text = "$days"
        itemView.countdown_hours.text = "$hours"
        itemView.countdown_minutes.text = "$minutes"
    }
}