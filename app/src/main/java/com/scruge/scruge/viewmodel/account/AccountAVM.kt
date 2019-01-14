package com.scruge.scruge.viewmodel.account

import android.os.Handler
import com.scruge.scruge.model.error.WalletError
import com.scruge.scruge.model.error.wrap
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.Settings
import com.scruge.scruge.services.wallet.AccountModel
import com.ysoftware.mvvm.array.SimpleArrayViewModel

class AccountAVM : SimpleArrayViewModel<AccountModel, AccountVM>() {

    override fun fetchData(block: (Result<Collection<AccountVM>>) -> Unit) {
        val wallet = Service.wallet.getAccount() ?: return block(Result.failure(WalletError.noKey.wrap()))

        Service.eos.getAccounts(wallet) { result ->
            result.onFailure { block(Result.failure(it)) }.onSuccess {
                if (it.isNotEmpty()) {
                    block(Result.success(it.map { AccountVM(AccountModel(it, wallet)) }.sorted()))
                }
                else {
                    block(Result.failure(WalletError.noAccounts.wrap()))
                }
            }
        }
    }

    // METHODS

    val selectedAccount:AccountVM? get() =
        Service.settings.get<String>(Settings.Setting.selectedAccount)
            ?.let { selected -> array.firstOrNull { it.name == selected }}

    fun deleteWallet() {
        Service.wallet.deleteWallet()
        Service.settings.remove(Settings.Setting.selectedAccount)
        Handler().postDelayed({ reloadData() }, 400)
    }
}