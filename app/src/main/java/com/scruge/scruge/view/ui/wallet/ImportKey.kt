package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.services.Service
import kotlinx.android.synthetic.main.fragment_wallet_import.*

class ImportKeyFragment: NavigationFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet_import, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        setupActions()
    }

    override fun viewDidAppear() {
        setupNavigationBar()
    }

    private fun setupViews() {
        wallet_import_save.title = "IMPORT KEY"
    }

    private fun setupActions() {
        wallet_import_save.click { save() }
        wallet_import_create.setOnClickListener { Service.presenter.replaceWithCreateAccountFragment(this) }
    }

    private fun setupNavigationBar() {
        title = "Import Private Key"
    }

    // METHODS

    private fun save() {
        val key = wallet_import_key.text.toString()
        val passcode = wallet_import_password.text.toString()

        if (key.isBlank())
            return alert("Enter your private key")

        if (key.length != 51 || !key.startsWith("5"))
            return alert("Incorrect formatDecimal of private key")

        if (passcode.isEmpty())
            return alert("Enter your new passcode")

        Service.wallet.deleteWallet()

        Service.wallet.importKey(key, passcode) { account ->
            if (account != null) {
                Handler().postDelayed({ Service.presenter.replaceWithWalletFragment(this) }, 400)
            }
            else {
                alert("Could not import this key")
            }
        }
    }
}