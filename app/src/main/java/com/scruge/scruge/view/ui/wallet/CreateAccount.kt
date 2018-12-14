package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.model.error.WalletError
import com.scruge.scruge.services.Service
import kotlinx.android.synthetic.main.fragment_wallet_create.*

class CreateAccountFragment: NavigationFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet_create, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        setupActions()
    }

    private fun setupViews() {
        wallet_create_save.title = "REGISTER"
    }

    private fun setupActions() {
        wallet_create_save.click { save() }
        wallet_create_import.setOnClickListener { Service.presenter.replaceWithImportKeyFragment(this) }
    }

    private fun save() {
        val name = wallet_create_name.text.toString()
        val passcode = wallet_create_password.text.toString()
        val confirm = wallet_create_confirm.text.toString()

        if (name.isBlank())
            return alert("Enter new account name")

        if (name.length != 12)
            return alert("Account name should be exactly 12 symbols long")

        if (passcode.isEmpty())
            return alert("Enter your new passcode")

        if (passcode != confirm)
            return alert("Passwords do not match")

        // todo refactor to view model

        // todo check if key exists

        Service.wallet.createKey(passcode) { account ->
            val publicKey = account?.rawPublicKey ?: return@createKey alert(WalletError.unknown)

            Service.api.createAccount(name, publicKey) { result ->
                result.onSuccess {
                    ErrorHandler.error(it.result)?.let {
                        alert(it)
                    } ?: Service.presenter.replaceWithWalletFragment(this)
                }.onFailure {
                    alert(ErrorHandler.message(it))
                }
            }
        }
    }
}