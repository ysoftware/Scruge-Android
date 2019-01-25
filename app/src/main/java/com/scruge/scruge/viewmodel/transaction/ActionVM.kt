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
import com.scruge.scruge.model.entity.ActionReceipt
import com.scruge.scruge.model.entity.VoteKind
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.eos.EosName
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
            ActionType.sent -> "Sent"
            ActionType.received -> "Received"
            ActionType.transfer -> "transfer"
            ActionType.invested -> "Invested"
            ActionType.voted -> "Voted"
            ActionType.other -> "${type.otherObject.account} -> ${type.otherObject.name}"
        }
    }

    val actionDescription:String? get() {
        val accountName = accountName?.toString() ?: return ""
        val model = model ?: return ""
        val type = ActionType.from(model.data.action_trace.act, accountName)

        return when (type) {
            ActionType.sent -> "${type.transferObj.quantity} to ${type.transferObj.to}"
            ActionType.received -> "${type.transferObj.quantity} from ${type.transferObj.from}"
            ActionType.transfer -> {
                val tr = type.transferObj
                "${tr.from} -> ${tr.to}: ${tr.quantity}"
            }
            ActionType.invested -> "${type.amount} in ${type.campaignTitle}"
            ActionType.voted -> {
                val ct = type.campaignTitle
                val vt = if (type.voteKind == VoteKind.extend) "extend deadline" else "release funds"
                "Participated in voting to $vt for campaign $ct"
            }
            ActionType.other -> null
        }
    }

    val actionDetails:String? get() {
        val accountName = accountName?.toString() ?: return ""
        val model = model ?: return ""
        val type = ActionType.from(model.data.action_trace.act, accountName)

        if (type == ActionType.other) {
            return type.otherObject.data.toString()  // todo parse
        }
        else if (type == ActionType.sent || type == ActionType.transfer || type == ActionType.received) {
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
                                type.campaignTitle = "-campaign-"
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
                type.campaignTitle = "-campaign-"
                type.voteKind = VoteKind.extend // todo
                return type
            }
            val type = other
            type.otherObject = action
            return type
        }
    }
}