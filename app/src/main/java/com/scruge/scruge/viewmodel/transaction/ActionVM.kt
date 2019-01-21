package com.scruge.scruge.viewmodel.transaction

import com.memtrip.eos.http.rpc.model.history.response.HistoricAccountAction
import com.scruge.scruge.dependencies.dataformatting.datePresent
import com.scruge.scruge.model.entity.ActionReceipt
import com.scruge.scruge.services.eos.EosName
import com.ysoftware.mvvm.single.ViewModel

class ActionVM(model: ActionReceipt?) : ViewModel<ActionReceipt>(model) {

    val accountName: EosName? = (arrayDelegate as? ActionsAVM)?.accountName

    val time:String get() {
        val model = model ?: return ""
        val date = model.data.block_time
        return datePresent(date.time, "d/MM/yy HH:mm")
    }

    // todo

    val actionName get() = model?.data?.action_trace?.act?.name ?: ""

    val actionDetails get() = model?.data?.action_trace?.act?.data?.toString() ?: ""
}