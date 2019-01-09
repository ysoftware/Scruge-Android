package com.scruge.scruge.viewmodel.account

import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.model.entity.Balance
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.wallet.AccountModel
import com.ysoftware.mvvm.single.ViewModel

class AccountVM(model: AccountModel):ViewModel<AccountModel>(model) {

    private var balances:List<Balance> = listOf()

    val name get() = model?.name ?: ""

    fun balanceString(separator:String = "\n", currency:String? = null):String {
        currency?.let {
            balances.find { it.symbol == currency }?.let {
                return "${it.symbol} ${it.amount}"
            } ?: return "$currency 0.0000"
        }

        return balances.fold("") { result, balance ->
            val amount = balance.amount.formatRounding(4, 4)
            val sep = if (result.isBlank()) "" else separator
            "$result$sep${balance.symbol} $amount"
        }
    }

    fun updateBalance() {
        Service.eos.getBalance(name, listOf("EOS", "SCR", "SYS")) { balances ->
            this.balances = balances.sorted()
            notifyUpdated()
        }
    }
}