package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.services.Service

class WalletFragment: NavigationFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
    }

    override fun onResume() {
        super.onResume()
        verifyWallet()
    }

    private fun setupVM() {

    }

    private fun verifyWallet() {
        if (!Service.wallet.hasAccount) {
            Service.presenter.replaceWithWalletStartFragment(this)
        }
    }
}