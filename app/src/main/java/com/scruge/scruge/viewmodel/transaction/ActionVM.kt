package com.scruge.scruge.viewmodel.transaction

import android.graphics.Color
import android.util.Log
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.internal.LinkedHashTreeMap
import com.memtrip.eos.chain.actions.transaction.transfer.actions.TransferArgs
import com.memtrip.eos.http.rpc.model.history.response.HistoricAccountAction
import com.memtrip.eos.http.rpc.model.transaction.response.TransactionAct
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.datePresent
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.ActionReceipt
import com.scruge.scruge.model.entity.VoteKind
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.eos.EosName
import com.scruge.scruge.support.App.Companion.context
import com.ysoftware.mvvm.single.ViewModel

class ActionVM(model: ActionReceipt?) : ViewModel<ActionReceipt>(model) {

    val accountName: EosName? get() = (arrayDelegate as? ActionsAVM)?.accountName

    val time:String get() {
        val model = model ?: return ""
        val date = model.data.block_time
        return datePresent(date.time, "d/MM/yy HH:mm")
    }

    val actionColor: Int get() {
        val accountName = accountName?.toString() ?: return R.color.text_gray
        val model = model ?: return R.color.text_gray
        val type = ActionType.from(model.data.action_trace.act, accountName)

        return when (type) {
            ActionType.sent -> R.color.purple
            ActionType.received -> R.color.green
            ActionType.invested -> R.color.purple
            ActionType.voted -> R.color.green
            else -> R.color.text_gray
        }
    }

    val actionName:String get() {
        val accountName = accountName?.toString() ?: return ""
        val model = model ?: return ""
        val type = ActionType.from(model.data.action_trace.act, accountName)

        return when (type) {
            ActionType.sent -> R.string.transaction_sent.string()
            ActionType.received -> R.string.transaction_received.string()
            ActionType.transfer -> R.string.transaction_transfer.string()
            ActionType.invested -> R.string.transaction_invested.string()
            ActionType.voted -> R.string.transaction_voted.string()
            ActionType.other -> "${type.otherObject.account} -> ${type.otherObject.name}"
        }
    }

    val actionDescription:String? get() {
        val accountName = accountName?.toString() ?: return ""
        val model = model ?: return ""
        val type = ActionType.from(model.data.action_trace.act, accountName)

        return when (type) {
            ActionType.sent ->
                R.string.transaction_received_from_description.string(type.transferObj.quantity,
                                                                      type.transferObj.to)
            ActionType.received ->
                R.string.transaction_received_from_description.string(type.transferObj.quantity,
                                                                      type.transferObj.from)
            ActionType.transfer -> {
                val tr = type.transferObj
                "${tr.from} -> ${tr.to}: ${tr.quantity}"
            }
            ActionType.invested ->
                R.string.transaction_invested_in_description.string(type.amount, type.campaignTitle)
            ActionType.voted -> {
                val ct = type.campaignTitle
                val vt = if (type.voteKind == VoteKind.extend) R.string.label_voting_to_extend.string()
                                        else R.string.label_voting_to_release_funds.string()
                R.string.label_voting_participated.string(vt, ct)
            }
            ActionType.other -> null
        }
    }

    val actionDetails:String? get() {
        val accountName = accountName?.toString() ?: return ""
        val model = model ?: return ""
        val type = ActionType.from(model.data.action_trace.act, accountName)

        if (type == ActionType.other) {
            return type.otherObject.data.toString()
        }
        else if (model.data.action_trace.act.name == "transfer") {
            if (type.transferObj.memo.isNotBlank()) {
                return type.transferObj.memo
            }
        }
        return null
    }
}

enum class ActionType {

    sent, received, transfer, invested, voted, other;

    lateinit var transferObj:TransferArgs

    lateinit var campaignTitle:String

    lateinit var amount:String

    lateinit var voteKind:VoteKind

    lateinit var otherObject: TransactionAct

    companion object {

        fun from(action: TransactionAct, accountName:String?):ActionType {
            if (action.name == "transfer") {
                (action.data as? Map<*, *>)?.let { transferData ->

                    val gson = Gson()
                    val json = gson.toJson(transferData)
                    try {
                    val data:TransferArgs = gson.fromJson(json, object : TypeToken<TransferArgs>() {}.type)

                        if (accountName == data.from) {
                            if (data.to == Service.eos.contractAccount.toString()) {
                                val type = invested
                                type.campaignTitle = "-campaign-" // todo
                                type.amount = data.quantity
                                return type
                            }
                            val type = sent
                            type.transferObj = data
                            return type
                        }
                        else if (accountName == data.to) {
                            val type = received
                            type.transferObj = data
                            return type
                        }
                        val type = transfer
                        type.transferObj = data
                        return type
                    }
                    catch (ex:Exception) {
                        ex.printStackTrace()
                    }
                }
            }
            else if (action.name == "vote" && action.account == Service.eos.contractAccount.toString()) {
                val type = voted
                type.campaignTitle = "-campaign-" // todo
                type.voteKind = VoteKind.extend // todo
                return type
            }
            val type = other
            type.otherObject = action
            return type
        }
    }
}