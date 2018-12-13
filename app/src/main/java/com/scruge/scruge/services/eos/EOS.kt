package com.scruge.scruge.services.eos

import com.memtrip.eos.http.rpc.Api
import com.memtrip.eos.http.rpc.model.history.request.GetKeyAccounts
import com.scruge.scruge.model.error.EOSError
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.model.error.WalletError
import com.scruge.scruge.model.error.wrap
import com.scruge.scruge.services.wallet.storage.LocalAccount
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class EOS {

    val service: Api

    init {
        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS).build()
        service = Api("http://35.242.241.205:7777", okHttpClient)
    }

    // METHODS

    fun getAccounts(wallet: LocalAccount, completion: (Result<List<String>>) -> Unit) {
        val key = wallet.rawPublicKey ?: return completion(Result.failure(WalletError.noKey.wrap()))

        service.history.getKeyAccounts(GetKeyAccounts(key))
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
}