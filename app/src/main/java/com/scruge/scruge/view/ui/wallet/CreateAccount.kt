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
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.setHidden
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

        setupKey()
        setupViews()
        setupActions()
    }

    override fun viewDidAppear() {
        setupNavigationBar()
    }

    private fun setupViews() {
        wallet_create_save.title = "REGISTER"
        val generate = "Generate a new keypair and we will use it to create a new EOS account for you."
        val imported = "Use the imported key to create a new EOS account."

        wallet_create_password_view.setHidden(Service.wallet.hasAccount)
        wallet_create_confirm_view.setHidden(Service.wallet.hasAccount)
        create_description.text = if (Service.wallet.hasAccount) imported else generate
    }

    private fun setupActions() {
        wallet_create_save.click { save() }
        wallet_create_import.setOnClickListener { Service.presenter.replaceWithImportKeyFragment(this) }
        wallet_create_shuffle.setOnClickListener { newKeypair() }
    }

    private fun setupNavigationBar() {
        title = "Create EOS Account"
    }

    private lateinit var key:EosPublicKey
    private var privateKey:EosPrivateKey? = null // null if key was imported earlier

    // METHODS

    private fun setupKey() {
        val account = Service.wallet.getAccount()
        wallet_create_shuffle.setHidden(account != null)

        if (account != null) {
            key = account.publicKey
            showKey()
        }
        else {
            newKeypair()
        }
    }

    private fun showKey() {
        wallet_create_key.text = "Public key:\n$key"
    }

    private fun newKeypair() {
        privateKey = EosPrivateKey()
        key = privateKey!!.publicKey
        showKey()
    }

    private fun save() {
        val name = wallet_create_name.text.toString()
        val passcode = wallet_create_password.text.toString()
        val confirm = wallet_create_confirm.text.toString()

        if (name.isBlank())
            return alert("Enter new account name")

        if (name.length != 12)
            return alert("Account name should be exactly 12 symbols long")

        privateKey?.let {

            if (passcode.isEmpty()) return alert("Enter your new passcode")

            if (passcode != confirm) return alert("Passwords do not match")

            Service.wallet.importKey(it.toString(), passcode) {
                if (it != null) {
                    createAccount(name, it.rawPublicKey)
                }
            }
        } ?: createAccount(name, key.toString())
    }

    private fun createAccount(name:String, publicKey:String) {
        Service.api.createAccount(name, publicKey) { result ->
            result.onSuccess {
                ErrorHandler.error(it.result)?.let {
                    alert(it)
                } ?: Handler().postDelayed({ Service.presenter.replaceWithWalletFragment(this) }, 1000)
            }.onFailure {
                alert(ErrorHandler.message(it))
            }
        }
    }
}