package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.dateToRelative
import com.scruge.scruge.dependencies.view.setHidden
import com.scruge.scruge.dependencies.view.setImage
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.VoteKind
import com.scruge.scruge.model.entity.Voting
import com.scruge.scruge.viewmodel.activity.ActivityType
import com.scruge.scruge.viewmodel.activity.ActivityVM
import com.scruge.scruge.viewmodel.update.UpdateVM
import kotlinx.android.synthetic.main.cell_activity.view.*
import kotlinx.android.synthetic.main.cell_activity_vote.view.*

class VoteNotificationCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var voting:Voting

    fun setup(vote: Voting):VoteNotificationCell {
        this.voting = vote
        val voteKindText =
                if (vote.voting.kind == VoteKind.extend.kind) R.string.label_voting_to_extend.string()
                else R.string.label_voting_to_release_funds.string()
        itemView.cell_activity_vote_text.text = R.string.label_voting_to_for.string(voteKindText,
                                                                                    vote.campaign.title)
        itemView.cell_activity_vote_date.text = dateToRelative(vote.voting.endTimestamp)
        return this
    }

    fun tap(tap:(Voting)->Unit):VoteNotificationCell {
        itemView.setOnClickListener { tap(voting) }
        return this
    }
}

class ActivityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private lateinit var vm:ActivityVM

    fun setup(vm: ActivityVM):ActivityViewHolder {
        this.vm = vm

        val date:String
        val title:String
        val description:String
        var updateTitle:String? = null
        var image:String? = null

        when (vm.type) {
            ActivityType.reply -> {
                date = vm.replyDate
                title = vm.replyAuthorName
                description = vm.replyText
            }
            ActivityType.update -> {
                date = vm.updateDate
                title = vm.updateTitle
                updateTitle = vm.updateCampaignTitle
                description = vm.updateDescription
                image = vm.updateImage
            }
            ActivityType.voting -> {
                date = vm.votingDate
                title = vm.votingTitle
                description = vm.votingDescription
            }
            ActivityType.fundingInfo -> {
                date = vm.fundingDate
                title = vm.fundingTitle
                description = vm.fundingDescription
            }
            ActivityType.votingResults -> {
                date = vm.votingResultDate
                title = vm.votingResultTitle
                description = vm.votingResultDescription
            }
            ActivityType.submission -> {
                date = vm.submissionDate
                title = vm.submissionTitle
                description = vm.submissionDetails
            }
            ActivityType.submissionPaid -> {
                date = vm.submissionPaidDate
                title = vm.submissionPaidTitle
                description = vm.submissionPaidDetails
            }
        }

        itemView.cell_activity_content_image.setImage(image)

        itemView.cell_activity_image.setImageResource(vm.icon)
        itemView.cell_activity_image_view.setBackgroundResource(vm.color)
        itemView.cell_activity_date.text = date
        itemView.cell_activity_campaign.text = title
        itemView.cell_activity_text.text = description
        // update title
        itemView.cell_activity_title.text = updateTitle
        itemView.cell_activity_title.setHidden(updateTitle == null)

        return this
    }

    fun showDecor(isFirst:Boolean, isLast:Boolean):ActivityViewHolder {
        // top
        itemView.activity_first_cell_decor.visibility = if (isLast) View.GONE else View.VISIBLE

        // bottom
        itemView.activity_last_cell_decor.visibility = if (isFirst) View.GONE else View.VISIBLE
        return this
    }

    fun campaignTap(tap:(Int)->Unit):ActivityViewHolder {
        when (vm.type) {
            ActivityType.fundingInfo, ActivityType.update, ActivityType.votingResults, ActivityType.voting ->
                itemView.cell_activity_title_view.setOnClickListener { vm.campaignId?.let { tap(it) }}
            else -> {}
        }
        return this
    }

    fun replyTap(tap:(String)->Unit):ActivityViewHolder {
        when (vm.type) {
            ActivityType.reply ->
                itemView.cell_activity_content_view.setOnClickListener { vm.replyId?.let { tap(it) }}
            else -> {}
        }
        return this
    }

    fun updateTap(tap: (UpdateVM)->Unit):ActivityViewHolder {
        when (vm.type) {
            ActivityType.update ->
                itemView.cell_activity_content_view.setOnClickListener { vm.updateVM?.let { tap(it) }}
            else -> {}
        }
        return this
    }
}