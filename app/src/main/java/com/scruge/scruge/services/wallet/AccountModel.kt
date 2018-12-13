package com.scruge.scruge.services.wallet

import com.memtrip.eos.chain.actions.transaction.TransactionContext
import com.scruge.scruge.services.wallet.storage.LocalAccount
import java.util.*

data class AccountModel(val name:String, val wallet:LocalAccount):Comparable<AccountModel> {

    override fun compareTo(other: AccountModel): Int = compareValuesBy(other, this) { it.name }

    fun getTransactionContext(account:String, passcode:String, completion:(TransactionContext?)->Unit) {
        wallet.retrievePrivateKey(passcode) { privateKey ->
            privateKey?.let {
                completion(TransactionContext(account, it, transactionDefaultExpiry()))
            } ?: completion(null)
        }
    }

    private fun transactionDefaultExpiry(): Date = with(Calendar.getInstance()) {
        set(Calendar.MINUTE, get(Calendar.MINUTE) + 2)
        this
    }.time
}