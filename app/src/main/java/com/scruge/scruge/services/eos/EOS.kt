package com.scruge.scruge.services.eos

import android.util.Log
import com.memtrip.eos.chain.actions.ChainResponse
import com.memtrip.eos.chain.actions.transaction.account.DelegateBandwidthChain
import com.memtrip.eos.chain.actions.transaction.transfer.TransferChain
import com.memtrip.eos.http.rpc.Api
import com.memtrip.eos.http.rpc.model.account.request.AccountName
import com.memtrip.eos.http.rpc.model.contract.request.GetCurrencyBalance
import com.memtrip.eos.http.rpc.model.history.request.GetActions
import com.memtrip.eos.http.rpc.model.history.request.GetKeyAccounts
import com.memtrip.eos.http.rpc.model.history.response.HistoricAccountAction
import com.memtrip.eos.http.rpc.model.transaction.response.TransactionCommitted
import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.model.entity.Balance
import com.scruge.scruge.model.entity.Resources
import com.scruge.scruge.model.error.EOSError
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.model.error.WalletError
import com.scruge.scruge.model.error.wrap
import com.scruge.scruge.services.wallet.AccountModel
import com.scruge.scruge.services.wallet.storage.LocalAccount
import com.scruge.scruge.viewmodel.transaction.ActionsQuery
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class EOS {

    private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

    val contractAccount = EosName.from("testaccount1")!!

    private val testNodeUrl = "http://35.242.241.205:7777"

    val isMainNet get() = nodeUrl != testNodeUrl

    var nodeUrl = testNodeUrl
        set(value) {
            field = value
            service = Api(nodeUrl, okHttpClient)
        }

    var service = Api(nodeUrl, okHttpClient); private set

    // METHODS

    fun getAccounts(wallet: LocalAccount, completion: (Result<List<String>>) -> Unit) {
        service.history.getKeyAccounts(GetKeyAccounts(wallet.rawPublicKey))
                .doOnError {
                    completion(Result.failure(it))
                }
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

    fun getResources(account:EosName, completion: (Result<Resources>)->Unit) {
        service.chain.getAccount(AccountName(account.toString()))
                .doOnError {
                    completion(Result.failure(it))
                }
                .subscribeOn(Schedulers.newThread())
                .subscribe({ response ->

                    response.body()?.let {
                        completion(Result.success(Resources(it)))
                    }
                }, { error ->
                    val e = ErrorHandler.error(error) ?: EOSError.unknown
                    completion(Result.failure(e.wrap()))
                })
    }

    fun getActions(account:EosName,
                   query: ActionsQuery?,
                   completion: (Result<List<HistoricAccountAction>>)->Unit) {
        val params = GetActions(account.toString(), query?.position, query?.offset)
        service.history.getActions(params)
                .doOnError {
                    completion(Result.failure(it))
                }
                .subscribeOn(Schedulers.newThread())
                .subscribe({ response ->
                    response.body()?.actions?.let {
                        val actions = it.sortedByDescending { it.global_action_seq }

                        if (!actions.isEmpty() && query?.position == -1L) {
                            query.setLimit(it.first().account_action_seq)
                        }
                        completion(Result.success(actions))
                    } ?: completion(Result.failure(EOSError.unknown.wrap()))
                           }, { error ->
                    val e = ErrorHandler.error(error) ?: EOSError.unknown
                    completion(Result.failure(e.wrap()))
                })
    }

    fun getBalance(account:EosName,
                   tokens:List<Token>,
                   requestAll:Boolean = false,
                   completion: (List<Balance>) -> Unit) {

        var i = 0
        val balances = arrayListOf<Balance>()

        tokens.distinct().forEach { token ->
            val sym = if (requestAll) null else token.symbol
            service.chain.getCurrencyBalance(GetCurrencyBalance(token.contract, account.toString(), sym))
                    .doOnError {
                        completion(listOf())
                    }
                    .subscribeOn(Schedulers.newThread())
                    .subscribe { response, _ ->
                        i += 1

                        val body = response.body()
                        if (body != null) {
                            if (body.isNotEmpty()) {
                                body.forEach {
                                    balances.add(Balance(it, token.contract))
                                }
                            }
                            else {
                                balances.add(Balance(token, 0.0))
                            }
                        }

                        if (i == tokens.size) {
                            completion(balances.sortedByDescending { it.amount })
                        }
                    }
        }
    }

    fun stakeResources(account: AccountModel,
                       cpu:String,
                       net:String,
                       passcode:String,
                       completion:(Result<String>)->Unit) {

        account.getTransactionContext(passcode) {
            val context = it ?: return@getTransactionContext completion(
                    Result.failure(WalletError.incorrectPasscode.wrap()))

            val args = DelegateBandwidthChain.Args(account.name, account.name, net, cpu, false)
            DelegateBandwidthChain(service.chain).delegateBandwidth(args, context).subscribe(completion)
        }
    }

    fun sendMoney(account: AccountModel,
                  recipient:EosName,
                  amount:Double,
                  token:Token,
                  memo:String,
                  passcode:String,
                  completion:(Result<String>)->Unit) {

        account.getTransactionContext(passcode) {
            val context = it ?:
            return@getTransactionContext completion(Result.failure(WalletError.incorrectPasscode.wrap()))

            val quantity = Balance(token, amount).toString()
            val args = TransferChain.Args(account.name, recipient.toString(), quantity, memo)

            TransferChain(service.chain).transfer(token.contract, args, context)
                    .subscribe(completion)
        }
    }

    fun <M:AbiConvertible>sendAction(action:EosName,
                                     contract:EosName = contractAccount,
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

            Action(service.chain).push(contract.toString(), action.toString(), data, permission, context)
                    .subscribe(completion)
        }
    }
}

fun Single<ChainResponse<TransactionCommitted>>.subscribe(completion: (Result<String>) -> Unit) {
    doOnError {
        completion(Result.failure(it))
    }
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