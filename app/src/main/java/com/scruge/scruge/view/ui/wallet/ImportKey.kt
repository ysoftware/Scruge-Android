package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.hideKeyboard
import com.scruge.scruge.dependencies.view.string
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
        wallet_import_save.title = R.string.do_import_key.string()
    }

    private fun setupActions() {
        wallet_import_save.click { save() }
        wallet_import_create.setOnClickListener {
            if (!Service.tokenManager.hasToken) {
                return@setOnClickListener alert(R.string.alert_sign_in_first.string())
            }
            Service.presenter.replaceWithCreateAccountFragment(this)
        }
    }

    private fun setupNavigationBar() {
        title = R.string.title_import_private_key.string()
    }

    // METHODS

    private fun save() {
        val key = wallet_import_key.text.toString()
        val passcode = wallet_import_password.text.toString()

        if (key.isBlank())
            return alert(R.string.error_enter_private_key.string())

        if (key.length != 51 || !key.startsWith("5"))
            return alert(R.string.error_incorrect_key_format.string())

        if (passcode.isEmpty())
            return alert(R.string.error_wallet_enter_wallet_password.string())

        hideKeyboard()
        Service.wallet.deleteWallet()

        Service.wallet.importKey(key, passcode) { account ->
            activity?.runOnUiThread {
                if (account != null) {
                    Handler().postDelayed({ Service.presenter.replaceWithWalletFragment(this) }, 400)
                }
                else {
                    alert("Could not import this key")
                }
            }
        }
    }
}