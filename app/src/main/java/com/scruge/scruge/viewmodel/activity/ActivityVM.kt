package com.scruge.scruge.viewmodel.activity

import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.datePresent
import com.scruge.scruge.dependencies.dataformatting.formatDecimal
import com.scruge.scruge.model.entity.*
import com.scruge.scruge.viewmodel.update.UpdateVM
import com.ysoftware.mvvm.single.ViewModel

enum class ActivityType {
    reply, update, voting, votingResults, fundingInfo;

    companion object {

        fun from(string: String?): ActivityType {
            return when (string) {
                "Reply" -> reply
                "Update" -> update
                "Voting" -> voting
                "CampFundingEnd" -> fundingInfo
                "VotingResult" -> votingResults
                else -> update
            }
        }
    }
}

class ActivityVM(model: ActivityModel?): ViewModel<ActivityModel>(model) {

    val type get() = ActivityType.from(model?.type)

    // general

    val icon get() = when (type) {
        ActivityType.update -> R.drawable.checkmark
        ActivityType.reply -> R.drawable.comment
        ActivityType.voting -> R.drawable.star
        ActivityType.fundingInfo -> R.drawable.checkmark
        ActivityType.votingResults -> R.drawable.star
    }

    val color get() = when (type) {
        ActivityType.update -> R.color.green
        ActivityType.reply -> R.color.purple
        ActivityType.voting -> R.color.green
        ActivityType.fundingInfo -> R.color.purple
        ActivityType.votingResults -> R.color.purple
    }

    // reply

    val replyDate get() = (model as? ActivityReply)?.timestamp?.let { datePresent(it, "d MMMM HH:mm") } ?: ""

    val replyText get() = (model as? ActivityReply)?.replyCommentText ?: ""

    val replyAuthorName get()
        = (model as? ActivityReply)?.replyUserName?.let { if (it.isNotBlank()) it else null }
                ?: "Anonymous" + " replied to your comment"

    // funding result

    val fundingDate get()
    = (model as? ActivityFunding)?.timestamp?.let { datePresent(it, "d MMMM HH:mm") } ?: ""

    val fundingTitle get()
    = (model as? ActivityFunding)?.let {
            "${it.campaign.title} has finished its funding campaign"
        } ?: ""

    val fundingDescription get()
        = (model as? ActivityFunding)?.let {
            val cap = it.softCap.formatDecimal(" ")
            val s = "${it.campaign.title}  has successfully reached the goal of $cap"
            val f = "${it.campaign.title} did not reach the minimum goal of $cap and is now closed."
            if (it.raised >= it.softCap) s else f
        } ?: ""

    // update

    val updateCampaignTitle get()
        = ((model as? ActivityUpdate)?.campaign?.title ?: "") + "  posted an update"

    val updateDescription get() = (model as? ActivityUpdate)?.update?.description ?: ""

    val updateImage get() = (model as? ActivityUpdate)?.update?.imageUrl

    val updateTitle get() = (model as? ActivityUpdate)?.update?.title ?: ""

    val updateDate get() = (model as? ActivityUpdate)?.update
            ?.timestamp?.let { datePresent(it, "d MMMM HH:mm") } ?: ""

    // voting

    val votingDate get()
        = (model as? ActivityVoting)?.timestamp?.let { datePresent(it, "d MMMM HH:mm") } ?: ""

    val votingTitle get()
        = (model as? ActivityVoting)?.let {
            val type = if (VoteKind.from(it.kind) == VoteKind.extend) "extend deadline" else "release funds"
            "Voting to $type for ${it.campaign.title} starts soon"
        } ?: ""

    val votingDescription get()
        = (model as? ActivityVoting)?.let {
            val date = datePresent(it.startTimestamp, "d MMMM")
            val time = datePresent(it.startTimestamp, "HH:mm")
            val period = it.noticePeriodSec / (24*60*60)
            val type = if (VoteKind.from(it.kind) == VoteKind.extend) "extend deadline" else "release funds"
            "Voting to $type of milestone ${it.milestoneTitle} for campaign ${it.campaign.title} starts in $period days on $date at $time"
        } ?: ""

    // voting result

    val votingResultDate get()
    = (model as? ActivityVotingResult)?.timestamp?.let { datePresent(it, "d MMMM HH:mm") } ?: ""

    val votingResultTitle get() // todo
    = (model as? ActivityVotingResult)?.let {
            val type = if (VoteKind.from(it.kind) == VoteKind.extend) "extend deadline" else "release funds"
            "Voting to $type for ${it.campaign.title} finished"
        } ?: ""

    val votingResultDescription get() // todo
    = (model as? ActivityVotingResult)?.let {
            val type = if (VoteKind.from(it.kind) == VoteKind.extend) "extend deadline" else "release funds"
            "Voting to $type of milestone ${it.milestoneTitle} for campaign ${it.campaign.title} finished..."
        } ?: ""

    // other

    val updateVM get() = (model as? ActivityUpdate)?.let { UpdateVM(it.update) }

    val replyId get() = (model as? ActivityReply)?.parentCommentId

    val campaignId get() = (model as? ActivityUpdate)?.campaign?.id
            ?: (model as? ActivityVoting)?.campaign?.id
            ?: (model as? ActivityFunding)?.campaign?.id
            ?: (model as? ActivityVotingResult)?.campaign?.id
}
