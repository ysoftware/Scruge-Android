package com.scruge.scruge.viewmodel.activity

import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.datePresent
import com.scruge.scruge.model.entity.ActivityModel
import com.scruge.scruge.model.entity.ActivityReply
import com.scruge.scruge.model.entity.ActivityUpdate
import com.ysoftware.mvvm.single.ViewModel

enum class ActivityType {
    reply, update, voting, fundingInfo;

    companion object {

        fun from(string: String?): ActivityType {
            return when (string) {
                "Reply" -> reply
                "Update" -> update
                "Voting" -> voting
                else -> update // todo add others
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
    }

    val color get() = when (type) {
        ActivityType.update -> R.color.green
        ActivityType.reply -> R.color.purple
        ActivityType.voting -> R.color.green
        ActivityType.fundingInfo -> R.color.purple
    }

    // voting

    val votingDate get() = "some date"

    val votingTitle get() = "Campaign's milestone deadline is coming up!"

    val votingDescription get() = "Voting on milestone starts in 3 days on Feb 7th 2019."

    // reply

    val replyDate get() = "some date" // (model as? ActivityReply)?.timestamp

    val replyText get() = (model as? ActivityReply)?.replyCommentText ?: ""

    val replyAuthorName get() = (model as? ActivityReply)?.replyUserName ?: ""

    // update

    val updateCampaignTitle get() = ((model as? ActivityUpdate)?.campaign?.title ?: "") + "  posted an update"

    val updateDescription get() = (model as? ActivityUpdate)?.update?.description ?: ""

    val updateTitle get() = (model as? ActivityUpdate)?.update?.title ?: ""

    val updateDate get() = (model as? ActivityUpdate)?.update
            ?.timestamp?.let { datePresent(it, "d MMMM yyyy") } ?: ""
}
