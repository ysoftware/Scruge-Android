package com.scruge.scruge.viewmodel.transaction

import com.scruge.scruge.model.entity.ActionReceipt
import com.scruge.scruge.model.error.EOSError
import com.scruge.scruge.model.error.wrap
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.eos.EosName
import com.scruge.scruge.services.eos.toEosName
import com.scruge.scruge.viewmodel.milestone.MilestoneVM
import com.ysoftware.mvvm.array.ArrayViewModel

class ActionsAVM(val accountName: EosName): ArrayViewModel<ActionReceipt, ActionVM, ActionsQuery>() {

    override fun fetchData(query: ActionsQuery?, block: (Result<Collection<ActionVM>>) -> Unit) {
        Service.eos.getActions(accountName, query) { result ->
            block(result.map { it.map { ActionVM(ActionReceipt(it)) } })
        }
    }
}