package com.scruge.scruge.viewmodel.transaction

import com.scruge.scruge.model.entity.ActionReceipt
import com.scruge.scruge.services.Service
import com.scruge.scruge.viewmodel.milestone.MilestoneVM
import com.ysoftware.mvvm.array.ArrayViewModel

class ActionsAVM(val accountName:String): ArrayViewModel<ActionReceipt, ActionVM, ActionsQuery>() {

    override fun fetchData(query: ActionsQuery?, block: (Result<Collection<ActionVM>>) -> Unit) {
        Service.eos.getActions(accountName, query) { result ->
            block(result.map { it.map { ActionVM(ActionReceipt(it)) } })
        }
    }
}