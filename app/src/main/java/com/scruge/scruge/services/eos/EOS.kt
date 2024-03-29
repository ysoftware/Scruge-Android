package com.scruge.scruge.services.eos

import android.util.Log
import com.memtrip.eos.chain.actions.ChainResponse
import com.memtrip.eos.chain.actions.transaction.account.*
import com.memtrip.eos.chain.actions.transaction.transfer.TransferChain
import com.memtrip.eos.chain.actions.transaction.vote.VoteChain
import com.memtrip.eos.http.rpc.Api
import com.memtrip.eos.http.rpc.model.account.request.AccountName
import com.memtrip.eos.http.rpc.model.contract.request.GetCurrencyBalance
import com.memtrip.eos.http.rpc.model.contract.request.GetTableRows
import com.memtrip.eos.http.rpc.model.history.request.GetActions
import com.memtrip.eos.http.rpc.model.history.request.GetKeyAccounts
import com.memtrip.eos.http.rpc.model.history.response.HistoricAccountAction
import com.memtrip.eos.http.rpc.model.info.Info
import com.memtrip.eos.http.rpc.model.producer.request.GetProducers
import com.memtrip.eos.http.rpc.model.producer.response.ProducerList
import com.memtrip.eos.http.rpc.model.transaction.response.TransactionCommitted
import com.scruge.scruge.model.entity.Balance
import com.scruge.scruge.model.entity.Resources
import com.scruge.scruge.model.error.EOSError
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.model.error.WalletError
import com.scruge.scruge.model.error.wrap
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.wallet.AccountModel
import com.scruge.scruge.services.wallet.storage.LocalAccount
import com.scruge.scruge.viewmodel.transaction.ActionsQuery
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class ContractAccounts {

    companion object {

        val BIDLMain = EosName.create("testaccount1")

        val bounty = EosName.create("scrugebounty")
    }
}

class EOS {

    val systemToken = Token.EOS

    private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()

    val testNodeUrl = "https://api-kylin.eosasia.one"

    val isMainNet get() = nodeUrl != testNodeUrl

    var nodeUrl = testNodeUrl
        set(value) {
            field = value
            service = Api(nodeUrl, okHttpClient)
        }

    var service = Api(nodeUrl, okHttpClient); private set

    // METHODS

    fun getChainInfo(url:String, completion: (Result<Info>) -> Unit) {
        service.chain.getInfo()
                .doOnError { completion(Result.failure(it)) }
                .subscribeOn(Schedulers.newThread())
                .subscribe({ response ->
                       val info = response.body()
                       if (info != null) {
                           completion(Result.success(info))
                       }
                       else {
                           completion(Result.failure(EOSError.unknown.wrap()))
                       }
                   }) { error ->
                    val e = ErrorHandler.error(error) ?: EOSError.unknown
                    completion(Result.failure(e.wrap()))
                }
    }

    fun getProducers(completion: (Result<ProducerList>) -> Unit) {
        val body = GetProducers(true, "", -1)
        service.chain.getProducers(body)
                .doOnError { completion(Result.failure(it)) }
                .subscribeOn(Schedulers.newThread())
                .subscribe({ response ->
                               val info = response.body()
                               if (info != null) {
                                   completion(Result.success(info))
                               }
                               else {
                                   completion(Result.failure(EOSError.unknown.wrap()))
                               }
                           }) { error ->
                    val e = ErrorHandler.error(error) ?: EOSError.unknown
                    completion(Result.failure(e.wrap()))
                }
    }

    fun getRamPrice(completion: (Result<Double>) -> Unit) {
        val params = GetTableRows("eosio", "eosio", "rammarket", "", true, 1, "", "", "", "", "")
        service.chain.getTableRows(params)
                .doOnError { completion(Result.failure(it)) }
                .subscribeOn(Schedulers.newThread())
                .subscribe({ response ->
                               val info = response.body()
                               if (info != null) {
                                   val rows = info.rows

                                   if (rows.isEmpty()) {
                                       return@subscribe completion(Result.failure(EOSError.unknown.wrap()))
                                   }

                                   val q = rows[0]["quote"] as? Map<String, Any>
                                           ?: return@subscribe completion(Result.failure(EOSError.unknown.wrap()))
                                   val quoteS = q["balance"] as? String
                                           ?: return@subscribe completion(Result.failure(EOSError.unknown.wrap()))
                                   val quote = quoteS.split(" ").firstOrNull()?.toDoubleOrNull()
                                           ?: return@subscribe completion(Result.failure(EOSError.unknown.wrap()))

                                   val b = rows[0]["base"] as? Map<String, Any>
                                           ?: return@subscribe completion(Result.failure(EOSError.unknown.wrap()))
                                   val baseS = b["balance"] as? String
                                           ?: return@subscribe completion(Result.failure(EOSError.unknown.wrap()))
                                   val base = baseS.split(" ").firstOrNull()?.toDoubleOrNull()
                                           ?: return@subscribe completion(Result.failure(EOSError.unknown.wrap()))

                                   completion(Result.success(quote / base))
                               }
                               else {
                                   completion(Result.failure(EOSError.unknown.wrap()))
                               }
                           }) { error ->
                    val e = ErrorHandler.error(error) ?: EOSError.unknown
                    completion(Result.failure(e.wrap()))
                }
    }

    fun getAccounts(wallet: LocalAccount, completion: (Result<List<String>>) -> Unit) {
        service.history.getKeyAccounts(GetKeyAccounts(wallet.rawPublicKey))
                .doOnError { completion(Result.failure(it)) }
                .subscribeOn(Schedulers.newThread())
                .subscribe({ response ->
                    val accounts = response.body()?.account_names
                    if (accounts != null) {
                        completion(Result.success(accounts))
                    }
                    else {
                        completion(Result.failure(EOSError.unknown.wrap()))
                    }
                }) { error ->
                    val e = ErrorHandler.error(error) ?: EOSError.unknown
                    completion(Result.failure(e.wrap()))
                }
    }

    fun getResources(account:EosName, completion: (Result<Resources>)->Unit) {
        service.chain.getAccount(AccountName(account.toString()))
                .doOnError { completion(Result.failure(it)) }
                .subscribeOn(Schedulers.newThread())
                .subscribe({ response ->

                    response.body()?.let {
                        completion(Result.success(Resources(it)))
                    }
                }) { error ->
                    val e = ErrorHandler.error(error) ?: EOSError.unknown
                    completion(Result.failure(e.wrap()))
                }
    }

    fun getActions(account:EosName,
                   query: ActionsQuery?,
                   completion: (Result<List<HistoricAccountAction>>)->Unit) {
        val params = GetActions(account.toString(), query?.position, query?.offset)
        service.history.getActions(params)
                .doOnError { completion(Result.failure(it)) }
                .subscribeOn(Schedulers.newThread())
                .subscribe({ response ->
                    response.body()?.actions?.let {
                        val actions = it.sortedByDescending { it.global_action_seq }

                        if (!actions.isEmpty() && query?.position == -1L) {
                            query.setLimit(it.first().account_action_seq)
                        }
                        completion(Result.success(actions))
                        } ?: completion(Result.failure(EOSError.unknown.wrap()))
                    }) { error ->
                        val e = ErrorHandler.error(error) ?: EOSError.unknown
                        completion(Result.failure(e.wrap()))
                    }
    }

    fun getBalance(account:EosName,
                   tokens:List<Token>,
                   requestAll:Boolean = false,
                   completion: (List<Balance>) -> Unit) {

        var i = 0
        val balances = arrayListOf<Balance>()
        val requestTokens = tokens.distinctBy { if (requestAll) it.contract else it }

        requestTokens.forEach { token ->
            val sym = if (!requestAll) token.symbol else null
            val params = GetCurrencyBalance(token.contract.toString(), account.toString(), sym)
            service.chain.getCurrencyBalance(params)
                    .doOnError { completion(listOf()) }
                    .subscribeOn(Schedulers.newThread())
                    .subscribe { response, _ ->
                        i += 1

                        val body = response.body()
                        if (body != null) {
                            if (body.isNotEmpty()) {
                                body.forEach {
                                    Balance.from(it, token.contract)?.let {
                                        if (requestAll || tokens.contains(it.token)) {
                                            if (!balances.contains(it)) {
                                                balances.add(it)
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                tokens.firstOrNull { it == token }?.let {
                                    balances.add(Balance(it, 0.0))
                                }
                            }
                        }
                        else {
                            tokens.firstOrNull { it.contract == token.contract }?.let {
                                balances.add(Balance(it, 0.0))
                            }
                        }

                        if (i == requestTokens.size) {
                            completion(balances.sortedByDescending { it.amount })
                        }
                    }
        }
    }

    fun stakeResources(account: AccountModel,
                       cpu:Balance,
                       net:Balance,
                       passcode:String,
                       completion:(Result<String>)->Unit) {

        if (cpu.token != net.token) {
            completion(Result.failure(EOSError.incorrectToken.wrap()))
            return
        }

        account.getTransactionContext(passcode) {
            val context = it ?: return@getTransactionContext completion(
                    Result.failure(WalletError.incorrectPasscode.wrap()))

            val args = DelegateBandwidthChain.Args(account.name, account.name,
                                                   net.toString(), cpu.toString(), false)
            DelegateBandwidthChain(service.chain).delegateBandwidth(args, context).subscribe(completion)
        }
    }

    fun unstakeResources(account: AccountModel,
                         cpu:Balance,
                         net:Balance,
                         passcode:String,
                         completion:(Result<String>)->Unit) {

        if (cpu.token != net.token) {
            completion(Result.failure(EOSError.incorrectToken.wrap()))
            return
        }

        account.getTransactionContext(passcode) {
            val context = it ?: return@getTransactionContext completion(
                    Result.failure(WalletError.incorrectPasscode.wrap()))

            val args = UnDelegateBandwidthChain.Args(account.name, account.name,
                                                     net.toString(), cpu.toString())
            UnDelegateBandwidthChain(service.chain).unDelegateBandwidth(args, context).subscribe(completion)
        }
    }

    fun buyRam(account: AccountModel,
               amount:Balance,
               passcode: String,
               completion: (Result<String>) -> Unit) {

        account.getTransactionContext(passcode) {
            val context = it ?: return@getTransactionContext completion(
                    Result.failure(WalletError.incorrectPasscode.wrap()))

            val args = BuyRamChain.Args(account.name, amount.toString())
            BuyRamChain(service.chain).buyRam(args, context).subscribe(completion)
        }
    }

    fun buyRam(account: AccountModel,
               bytes:Long,
               passcode: String,
               completion: (Result<String>) -> Unit) {

        account.getTransactionContext(passcode) {
            val context = it ?: return@getTransactionContext completion(
                    Result.failure(WalletError.incorrectPasscode.wrap()))

            val args = BuyRamBytesChain.Args(account.name, bytes)
            BuyRamBytesChain(service.chain).buyRamBytes(args, context).subscribe(completion)
        }
    }

    fun sellRam(account: AccountModel,
                bytes:Long,
                passcode: String,
                completion: (Result<String>) -> Unit) {
        account.getTransactionContext(passcode) {
            val context = it ?: return@getTransactionContext completion(
                    Result.failure(WalletError.incorrectPasscode.wrap()))

            val args = SellRamChain.Args(bytes)
            SellRamChain(service.chain).sellRam(args, context).subscribe(completion)
        }
    }

    fun sendMoney(account: AccountModel,
                  recipient:EosName,
                  balance: Balance,
                  memo:String,
                  passcode:String,
                  completion:(Result<String>)->Unit) {

        account.getTransactionContext(passcode) {
            val context = it ?:
            return@getTransactionContext completion(Result.failure(WalletError.incorrectPasscode.wrap()))

            val args = TransferChain.Args(account.name, recipient.toString(), balance.toString(), memo)

            TransferChain(service.chain).transfer(balance.token.contract.toString(), args, context)
                    .subscribe(completion)
        }
    }

    fun voteProducers(account: AccountModel,
                      names:Set<EosName>,
                      passcode: String,
                      completion: (Result<String>) -> Unit) {

        account.getTransactionContext(passcode) {
            val context = it ?: return@getTransactionContext completion(
                    Result.failure(WalletError.incorrectPasscode.wrap()))

            val bps = names.map { it.toString() }.sorted()

            val args = VoteChain.Args(account.name, "", bps)
            VoteChain(service.chain).vote(args, context).subscribe(completion)
        }
    }

    fun bountySubmit(account: AccountModel,
                     proof:String,
                     providerName:String,
                     bountyId:Long,
                     passcode:String,
                     completion: (Result<String>) -> Unit) {

        val data = Submission(account.name, providerName, proof, bountyId)

        Service.eos.sendAction(EosName.create("submit"),
                               contract = ContractAccounts.bounty,
                               account = account,
                               data = data,
                               passcode = passcode,
                               completion = completion)
    }

    fun <M:AbiConvertible>sendAction(action:EosName,
                                     contract:EosName = ContractAccounts.BIDLMain,
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
                   val e = ErrorHandler.parse(response.errorBody) ?: EOSError.unknown
                   completion(Result.failure(e.wrap()))
               }
           }) { error ->
               val e = ErrorHandler.error(error) ?: EOSError.unknown
               completion(Result.failure(e.wrap()))
           }
}