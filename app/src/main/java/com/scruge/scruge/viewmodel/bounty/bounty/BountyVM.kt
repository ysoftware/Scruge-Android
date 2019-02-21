package com.scruge.scruge.view.ui.bounty

import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.datePresent
import com.scruge.scruge.dependencies.dataformatting.formatDecimal
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.services.Time
import com.ysoftware.mvvm.single.ViewModel

class BountyVM(model: Bounty?, val tokenSupply:Long?) : ViewModel<Bounty>(model) {

    val id:Long? get() = model?.bountyId

    val name:String get() = model?.bountyName ?: ""

    val description:String get() = model?.bountyDescription?.trim() ?: ""

    val rewards:String get() = model?.rewardsDescription?.trim() ?: ""

    val rules:String get() = model?.rulesDescription?.trim() ?: ""

    val dates:String get() {
        val timestamp = model?.timestamp ?: return ""
        val endTimestamp = model?.endTimestamp ?: return ""
        val start = datePresent(timestamp, "d MMM yyyy")
        val end = datePresent(endTimestamp, "d MMM yyyy")
        return "$start - $end"
    }

    val shortDescription:String get() {
        val model = model ?: return ""

        val maxReward = getMaxReward()
        if (maxReward != null) {
            return maxReward
        }

        return R.string.label_bounty_submissions.string() +
                ": " +
                "${model.submissions}/${model.submissionLimit}" +
                "\n" +

                R.string.label_bounty_limit_per_user.string() +
                ": " +
                "${model.limitPerUser}"
    }

    val longerDescription:String get() {
        val model = model ?: return ""
        val timeLimitDays = model.resubmissionPeriodMilliseconds / Time.day

        var str = R.string.label_bounty_submissions.string() +
                ": " +
                "${model.submissions}/${model.submissionLimit}" +
                "\n" +

                R.string.label_bounty_limit_per_user.string() +
                ": " +
                "${model.limitPerUser}" +
                "\n" +

                R.string.label_bounty_resubmission_period.string() +
                ": " +
                "$timeLimitDays" +
                R.string.label_days_short_no_plural.string()

        getMaxReward()?.let {
            str = str + "\n" + it
        }

        return str
    }

    fun getMaxReward():String? {
        val model = model ?: return null
        val maxReward = model.maxReward ?: return null
        val totalSupply = tokenSupply ?: return null
        val array = maxReward.split(" ")

        if (array.size == 2) {
            val amount = array[0].toDoubleOrNull() ?: return null
            val percent = (amount / (totalSupply.toDouble() / 100)).formatDecimal()

            return R.string.label_bounty_max_reward.string() +
                    ": " +
                    maxReward +
                    " (" +
                    R.string.label_bounty_percent_of_total_supply.string(percent) +
                    ")"
        }
        return null
    }
}