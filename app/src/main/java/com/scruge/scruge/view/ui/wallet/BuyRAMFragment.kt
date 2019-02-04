package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.account.AccountVM
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.single.ViewModelDelegate

class BuyRAMFragment: NavigationFragment(), ArrayViewModelDelegate, ViewModelDelegate {

    var accountVM: AccountVM? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_buyram, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()

        setupNavigationBar()
    }

    override fun viewDidDisappear() {
        super.viewDidDisappear()
    }

    fun setupNavigationBar() {
        (activity as? TabbarActivity)?.tabbarHidden = false
        shouldHideNavigationBar = true
        title = R.string.title_buy_ram.string()
    }

    fun setupVM() {

    }
}