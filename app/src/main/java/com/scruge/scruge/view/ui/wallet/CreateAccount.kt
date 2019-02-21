package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.EosPublicKey
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.*
import com.scruge.scruge.model.error.EOSError
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.model.error.WalletError
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.eos.EosName
import com.scruge.scruge.services.eos.toEosName
import kotlinx.android.synthetic.main.fragment_wallet_create.*

class CreateAccountFragment: NavigationFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet_create, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupKey()
        setupViews()
        setupActions()
    }

    override fun viewDidAppear() {
        setupNavigationBar()
    }

    private fun setupViews() {
        wallet_create_save.title = R.string.do_create_account_cap.string()
        val generate = R.string.label_create_account_generate_text.string()
        val imported = R.string.label_create_account_use_imported_text.string()

        wallet_create_password_view.setHidden(Service.wallet.hasAccount)
        wallet_create_confirm_view.setHidden(Service.wallet.hasAccount)
        create_description.text = if (Service.wallet.hasAccount) imported else generate
    }

    private fun setupActions() {
        wallet_create_save.click { save() }
        wallet_create_import.setOnClickListener {
            val t = R.string.title_sure_to_delete_wallet.string()
            val q = R.string.label_sure_to_delete_wallet.string()

            if (Service.wallet.hasAccount) {
                ask(t, q) { r ->
                    if (r) {
                        Service.wallet.deleteWallet()
                        Service.presenter.replaceWithWalletStartFragment(this)
                    }
                }
            }
            else {
                Service.presenter.replaceWithImportKeyFragment(this)
            }
        }
        wallet_create_shuffle.setOnClickListener { newKeypair() }
    }

    private fun setupNavigationBar() {
        title = R.string.title_create_eos_account.string()
    }

    private lateinit var publicKey:EosPublicKey
    private var privateKey:EosPrivateKey? = null // null if publicKey was imported earlier

    // METHODS

    private fun setupKey() {
        val account = Service.wallet.getAccount()
        wallet_create_shuffle.setHidden(account != null)

        if (account != null) {
            publicKey = account.publicKey
            showKey()
        }
        else {
            newKeypair()
        }
    }

    private fun showKey() {
        wallet_create_key.text = R.string.public_key_display.string(publicKey.toString())
    }

    private fun newKeypair() {
        privateKey = EosPrivateKey()
        publicKey = privateKey!!.publicKey
        showKey()
    }

    private fun save() {
        if (!Service.tokenManager.hasToken) {
            return alert(R.string.alert_sign_in_first.string())
        }

        val name = wallet_create_name.text.toString()
        val passcode = wallet_create_password.text.toString()
        val confirm = wallet_create_confirm.text.toString()

        if (name.isBlank())
            return alert(R.string.error_enter_eos_account_name.string())

        if (name.length != 12)
            return alert(R.string.error_eos_account_length.string())

        val eosName = name.toEosName() ?: return alert(EOSError.incorrectName)

        if (name.contains("."))
            return alert(R.string.error_eos_account_cant_contain_dot.string())

        privateKey?.let {

            if (passcode.isEmpty()) return alert(R.string.error_enter_new_password.string())

            if (passcode != confirm) return alert(R.string.error_register_passwords_do_not_match.string())

            Service.wallet.importKey(it.toString(), passcode) {
                if (it != null) {
                    createAccount(eosName, it.publicKey)
                }
                else {
                    alert(WalletError.unknown)
                }
            }
        } ?: createAccount(eosName, publicKey)

        hideKeyboard()
    }

    private fun createAccount(name:EosName, publicKey:EosPublicKey) {
        wallet_create_save.isBusy = true
        
        Service.api.createAccount(name, publicKey) { result ->
            wallet_create_save.isBusy = false

            result.onSuccess {
                ErrorHandler.error(it.result)?.let {
                    return@onSuccess alert(it)
                }
                Service.settings.setDidCreateEosAccount()
                alert(R.string.alert_eos_account_created.string()) {
                    Handler().postDelayed({ Service.presenter.replaceWithWalletFragment(this) }, 1000)
                }
            }.onFailure {
                alert(ErrorHandler.message(it))
            }
        }
    }
}