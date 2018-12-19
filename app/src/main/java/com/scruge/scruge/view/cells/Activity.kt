package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.dependencies.dataformatting.toRelative
import com.scruge.scruge.model.entity.VoteKind
import com.scruge.scruge.model.entity.Voting
import com.scruge.scruge.viewmodel.update.UpdateVM
import kotlinx.android.synthetic.main.cell_activity.view.*
import kotlinx.android.synthetic.main.cell_activity_vote.view.*
import java.util.*

class VotingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var voting:Voting

    fun setup(vote: Voting):VotingViewHolder {
        this.voting = vote
        val voteKindText = if (vote.voting.kind == VoteKind.extend.kind) "extend deadline" else "continue campaign"
        itemView.cell_activity_vote_text.text = "Voting to $voteKindText for ${vote.campaign.title}"
        itemView.cell_activity_vote_date.text = Date(vote.voting.endTimestamp).toRelative()
        return this
    }

    fun tap(tap:(Voting)->Unit):VotingViewHolder {
        itemView.setOnClickListener { tap(voting) }
        return this
    }
}

class ActivityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private lateinit var vm:UpdateVM

    fun setup(vm: UpdateVM):ActivityViewHolder {
        this.vm = vm

        itemView.cell_activity_date.text = vm.date
        itemView.cell_activity_title.text = vm.title
        itemView.cell_activity_text.text = vm.description
        // todo image?

        itemView.cell_activity_campaign.text = "${vm.campaignTitle}  posted an update"

        return this
    }

    fun showDecor(isFirst:Boolean, isLast:Boolean):ActivityViewHolder {
        itemView.activity_first_cell_decor.visibility = if (isLast) View.GONE else View.VISIBLE
        itemView.activity_last_cell_decor.visibility = if (isFirst) View.GONE else View.VISIBLE
        return this
    }

    fun campaignTap(tap:(UpdateVM)->Unit):ActivityViewHolder {
        itemView.cell_activity_campaign.setOnClickListener { tap(vm) }
        return this
    }

    fun activityTap(tap:(UpdateVM)->Unit):ActivityViewHolder {
        itemView.cell_activity_view.setOnClickListener { tap(vm) }
        return this
    }
}