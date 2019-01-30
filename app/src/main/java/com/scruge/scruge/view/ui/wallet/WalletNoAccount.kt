package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.*
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
        wallet_no_accounts_button.title = R.string.do_create_account.string().toUpperCase()
    }

    private fun setupNavigationBar() {
        title = R.string.title_wallet.string()
    }

    private fun setupActions() {
        wallet_no_accounts_privacy.setOnClickListener { Service.presenter.presentPrivacyPolicy(this) }

        wallet_no_accounts_export.setOnClickListener {
            wallet_no_accounts_data.toggleHidden()
            wallet_no_accounts_data.updateViews()
            wallet_no_accounts_data.lock()
            wallet_no_accounts_export.text =
                    if (wallet_no_accounts_data.isHidden) R.string.do_see_wallet_data.string()
                    else R.string.do_hide_wallet_data.string()
        }

        wallet_no_accounts_remove.setOnClickListener {
            val t = R.string.title_sure_to_delete_wallet.string()
            val q = R.string.label_sure_to_delete_wallet.string()

            ask(t, q) { r ->
                if (r) {
                    Service.wallet.deleteWallet()
                    Service.presenter.replaceWithWalletStartFragment(this)
                }
            }
        }
        wallet_no_accounts_button.click {
            if (!Service.tokenManager.hasToken) {
                return@click alert(R.string.alert_sign_in_first.string())
            }
            Service.presenter.presentCreateAccountFragment(this)
        }
    }
}