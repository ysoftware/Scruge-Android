package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.ask
import com.scruge.scruge.dependencies.view.isHidden
import com.scruge.scruge.dependencies.view.toggleHidden
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

    override fun onPause() {
        super.onPause()
        wallet_no_accounts_data?.lock()
    }

    override fun viewDidDisappear() {
        super.viewDidDisappear()
        wallet_no_accounts_data?.lock()
    }

    private fun setupViews() {
        wallet_no_accounts_button.title = "CREATE ACCOUNT"
    }

    private fun setupNavigationBar() {
        title = "Wallet"
    }

    private fun setupActions() {
        wallet_no_accounts_export.setOnClickListener {
            wallet_no_accounts_data.toggleHidden()
            wallet_no_accounts_data.updateViews()
            wallet_no_accounts_data.lock()
            wallet_no_accounts_export.text = if (wallet_no_accounts_data.isHidden) "See wallet data" else "Hide wallet data"
        }

        wallet_no_accounts_remove.setOnClickListener {
            val t = "Are you sure to delete your wallet information?"
            val q = "Make sure to export your private key first because there is no way it can be retrieved later."

            ask(t, q) { r ->
                if (r) {
                    Service.wallet.deleteWallet()
                    Service.presenter.replaceWithWalletStartFragment(this)
                }
            }
        }
        wallet_no_accounts_button.click {
            if (!Service.tokenManager.hasToken) {
                return@click alert("Please sign in with your Scruge account first.")
            }
            Service.presenter.presentCreateAccountFragment(this)
        }
    }
}