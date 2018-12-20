package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.services.Service
import kotlinx.android.synthetic.main.fragment_wallet_no_accounts.*

class WalletNoAccountFragment: NavigationFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet_no_accounts, container, false)
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
        wallet_no_accounts_button.title = "CREATE ACCOUNT"
    }

    private fun setupNavigationBar() {
        title = "Wallet"
    }

    private fun setupActions() {
        wallet_no_accounts_remove.setOnClickListener {
            Service.wallet.deleteWallet()
            Service.presenter.replaceWithImportKeyFragment(this)
        }
        wallet_no_accounts_button.click {
            Service.presenter.presentCreateAccountFragment(this)
        }
    }
}