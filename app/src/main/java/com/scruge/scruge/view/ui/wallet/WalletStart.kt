package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.services.Service
import kotlinx.android.synthetic.main.fragment_wallet_start.*

class WalletStartFragment: NavigationFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet_start, container, false)
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
        wallet_start_button.title = R.string.do_import_key.string()
    }

    private fun setupNavigationBar() {
        title = R.string.title_wallet.string()
    }

    private fun setupActions() {
        wallet_start_privacy.setOnClickListener { Service.presenter.presentPrivacyPolicy(this) }
        wallet_start_button.click { Service.presenter.presentImporKeyFragment(this) }

        wallet_start_create.setOnClickListener {
            if (!Service.tokenManager.hasToken) {
                return@setOnClickListener alert(R.string.alert_sign_in_first.string())
            }
            Service.presenter.presentCreateAccountFragment(this)
        }

    }
}