package com.scruge.scruge.viewmodel.account

import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.model.entity.Balance
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.eos.Token
import com.scruge.scruge.services.eos.toEosName
import com.scruge.scruge.services.wallet.AccountModel
import com.ysoftware.mvvm.single.ViewModel

class AccountVM(model: AccountModel):ViewModel<AccountModel>(model) {

    private var balances:List<Balance> = listOf()

    val name get() = model?.name?.toEosName()

    val displayName get() = name?.toString() ?: ""

    fun balanceString(separator:String = "\n"):String {
        return balances.fold("") { result, balance ->
            val sep = if (result.isBlank()) "" else separator
            "$result$sep$balance"
        }
    }

    fun updateBalance() {
        val name = name ?: return
        Service.eos.getBalance(name, listOf(Token.EOS, Token.Scruge)) { balances ->
            this.balances = balances.sorted()
            notifyUpdated()
        }
    }
}