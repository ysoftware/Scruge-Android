package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
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
        wallet_start_button.title = "ADD ACCOUNT"
    }

    private fun setupNavigationBar() {
        title = "Wallet"
    }

    private fun setupActions() {
        wallet_start_create.setOnClickListener {
            Service.presenter.presentCreateAccountFragment(this)
        }
        wallet_start_button.click {
            Service.presenter.presentImporKeyFragment(this)
        }
    }
}