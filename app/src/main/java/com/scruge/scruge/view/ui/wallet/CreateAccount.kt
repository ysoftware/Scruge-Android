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
import com.scruge.scruge.dependencies.view.hideKeyboard
import com.scruge.scruge.dependencies.view.setHidden
import com.scruge.scruge.model.error.EOSError
import com.scruge.scruge.model.error.ErrorHandler
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
        wallet_create_save.title = "REGISTER"
        val generate = "Generate a new keypair and we will use it to create a new EOS account for you."
        val imported = "Use the imported publicKey to create a new EOS account."

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
        wallet_create_key.text = "Public key:\n$publicKey"
    }

    private fun newKeypair() {
        privateKey = EosPrivateKey()
        publicKey = privateKey!!.publicKey
        showKey()
    }

    private fun save() {
        val name = wallet_create_name.text.toString()
        val passcode = wallet_create_password.text.toString()
        val confirm = wallet_create_confirm.text.toString()

        if (name.isBlank())
            return alert("Enter new account name")

        if (name.length != 12)
            return alert("New account name has to be exactly 12 symbols long")

        val eosName = name.toEosName() ?: return alert(EOSError.incorrectName)

        if (name.contains("."))
            return alert("We can not create an account name that contains a dot")

        privateKey?.let {

            if (passcode.isEmpty()) return alert("Enter your new passcode")

            if (passcode != confirm) return alert("Passwords do not match")

            Service.wallet.importKey(it.toString(), passcode) {
                if (it != null) {
                    createAccount(eosName, it.publicKey)
                }
                else {
                    alert("An error occured. Please try again")
                }
            }
        } ?: createAccount(eosName, publicKey)

        hideKeyboard()
    }

    private fun createAccount(name:EosName, publicKey:EosPublicKey) {
        Service.api.createAccount(name, publicKey) { result ->
            result.onSuccess {
                ErrorHandler.error(it.result)?.let {
                    return@onSuccess alert(it)
                }
                alert("Your account have been created.\n\nMake sure to save your private publicKey in a safe place!\n\nIf you lose the publicKey, your account will be lost forever!")
                Handler().postDelayed({ Service.presenter.replaceWithWalletFragment(this) }, 1000)
            }.onFailure {
                alert(ErrorHandler.message(it))
            }
        }
    }
}