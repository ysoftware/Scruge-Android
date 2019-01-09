package com.scruge.scruge.model.entity

import com.memtrip.eos.http.rpc.model.account.response.Account
import com.memtrip.eos.http.rpc.model.history.response.HistoricAccountAction

data class ActionReceipt(val data: HistoricAccountAction):Comparable<ActionReceipt> {
    override fun compareTo(other: ActionReceipt): Int
            = compareValuesBy(this, other) { it.data.global_action_seq }
}

data class Resources(val data: Account): Comparable<Resources> {
    override fun compareTo(other: Resources): Int
        = compareValuesBy(this, other) { it.data.account_name }
}