package com.scruge.scruge.view.ui.bounty

data class Bounty(val bountyName: String?, val bountyId: Long, val providerName: String,
                  val bountyDescription: String, val rewardsDescription: String,
                  val rulesDescription: String, val limitPerUser: Int,
                  val timeLimit: Long, val userLimit: Int, val budget: String,
                  val endTimestamp: Long, val paid: String, val paidEOS: String,
                  val submissions: Int, val participantsPaid: Int, val timestamp: Long,
                  val totalSupply: Long?, val maxReward: String?) : Comparable<Bounty> {

    override fun compareTo(other: Bounty): Int = compareValuesBy(this, other) { it.bountyId }
}
