package com.scruge.scruge.services.eos

import android.util.Log
import com.memtrip.eos.chain.actions.ChainResponse
import com.memtrip.eos.chain.actions.transaction.transfer.TransferChain
import com.memtrip.eos.http.rpc.Api
import com.memtrip.eos.http.rpc.model.contract.request.GetCurrencyBalance
import com.memtrip.eos.http.rpc.model.history.request.GetKeyAccounts
import com.memtrip.eos.http.rpc.model.transaction.response.TransactionCommitted
import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.model.entity.Balance
import com.scruge.scruge.model.error.EOSError
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.model.error.WalletError
import com.scruge.scruge.model.error.wrap
import com.scruge.scruge.services.wallet.AccountModel
import com.scruge.scruge.services.wallet.storage.LocalAccount
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.lang.Exception
import java.util.concurrent.TimeUnit

class EOS {

    val contractAccount = "testaccount1"

    val service: Api

    init {
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(30, TimeUnit.SECONDS)
                .build()
        service = Api("http://35.242.241.205:7777", okHttpClient)
    }

    // METHODS

    fun getAccounts(wallet: LocalAccount, completion: (Result<List<String>>) -> Unit) {
        service.history.getKeyAccounts(GetKeyAccounts(wallet.rawPublicKey))
                .subscribeOn(Schedulers.newThread())
                .subscribe({ response ->
                    val accounts = response.body()?.account_names
                    if (accounts != null) {
                        completion(Result.success(accounts))
                    }
                    else {
                        completion(Result.failure(EOSError.unknown.wrap()))
                    }
                }, { error ->
                    val e = ErrorHandler.error(error) ?: EOSError.unknown
                    completion(Result.failure(e.wrap()))
        })
    }

    fun sendMoney(account: AccountModel,
                  recipient:String,
                  amount:Double,
                  symbol:String,
                  memo:String,
                  passcode:String,
                  completion:(Result<String>)->Unit) {

            account.getTransactionContext(passcode) {
                val context = it ?:
                return@getTransactionContext completion(Result.failure(WalletError.incorrectPasscode.wrap()))

                val quantity = "${amount.formatRounding(4, 4)} $symbol".replace(",", ".")
                val args = TransferChain.Args(account.name, recipient, quantity, memo)

                TransferChain(service.chain).transfer("eosio.token", args, context)
                        .subscribe(completion)
            }
    }

    fun <M:AbiConvertible>sendAction(action:String,
                                     contract:String = contractAccount,
                                     account:AccountModel,
                                     data:M,
                                     passcode:String,
                                     permission:String = "active",
                                     completion: (Result<String>) -> Unit) {
        account.getTransactionContext(passcode) { context ->
            if (context == null) {
                completion(Result.failure(WalletError.incorrectPasscode.wrap()))
                return@getTransactionContext
            }

            Action(service.chain).push(contract, action, data, permission, context)
                    .subscribe(completion)
        }
    }

    fun getBalance(account:String, currencies:List<String>, completion: (List<Balance>) -> Unit) {

        var i = 0
        val balances = arrayListOf<Balance>()

        currencies.forEach { currency ->
            service.chain.getCurrencyBalance(GetCurrencyBalance("eosio.token", account, currency))
                    .subscribeOn(Schedulers.newThread())
                    .subscribe { response, _ ->

                        i += 1

                        val body = response.body()
                        if (body != null) {
                            if (body.isNotEmpty()) {
                                balances.add(Balance(body.first()))
                            }
                            else {
                                balances.add(Balance(currency, 0.0))
                            }
                        }

                        if (i == currencies.size) {
                            completion(balances)
                        }
                    }
        }
    }
}

fun Single<ChainResponse<TransactionCommitted>>.subscribe(completion: (Result<String>) -> Unit) {
    subscribeOn(Schedulers.newThread())
            .subscribe({ response ->
                           val id = response.body?.transaction_id
                           if (id != null) {
                               completion(Result.success(id))
                           }
                           else {
                               Log.e("EOS", response.errorBody)
                               completion(Result.failure(EOSError.unknown.wrap()))
                           }
                       }, { error ->
                           val e = ErrorHandler.error(error) ?: EOSError.unknown
                           completion(Result.failure(e.wrap()))
                       })
}