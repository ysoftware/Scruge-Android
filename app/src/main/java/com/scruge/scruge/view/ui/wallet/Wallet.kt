package com.scruge.scruge.view.ui.wallet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.model.ViewState
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.model.error.WalletError
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.Settings
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.account.AccountAVM
import com.scruge.scruge.viewmodel.account.AccountVM
import com.ysoftware.mvvm.array.*
import com.ysoftware.mvvm.single.ViewModel
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_wallet.*

class WalletFragment: NavigationFragment(), ArrayViewModelDelegate, ViewModelDelegate {

    private val vm = AccountAVM()
    private var accountVM: AccountVM? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
        setupActions()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()

        (activity as? TabbarActivity)?.tabbarHidden = false
        verifyWallet()
    }

    private fun setupVM() {
        vm.delegate = this
        vm.reloadData()
    }

    private fun setupActions() {
        wallet_settings.setOnClickListener {
            Service.presenter.presentWalletPicker(this)
        }
    }

    private fun updateView() {
        wallet_account_name.text = accountVM?.name ?: ""
        wallet_balance.text = accountVM?.balanceString() ?: ""
    }

    private fun updateStatus() {
        when (vm.state) {
            State.error -> {
                val error = ErrorHandler.error(vm.state.errorValue)
                when (error) {
                    WalletError.noKey ->
                        Service.presenter.replaceWithWalletStartFragment(this)
                    WalletError.noAccounts ->
                        Service.presenter.replaceWithWalletNoAccountFragment(this)
                    else -> {
                        val e = ViewState.error
                        e.errorMessage = ErrorHandler.message(error)
                        wallet_loading_view?.state = e
                    }
                }
            }
            State.loading -> {
                wallet_loading_view?.state = ViewState.loading
            }
            State.ready -> {
                wallet_loading_view?.state = ViewState.ready
            }
            else -> { }
        }
    }

    private fun presentWalletPicker() {
        if (vm.isEmpty()) { return }

        if (vm.numberOfItems == 1) {
            Service.settings.set(Settings.Setting.selectedAccount, vm.item(0).name)
            vm.reloadData()
            return
        }

        Service.presenter.presentWalletPicker(this)
    }

    private fun selectVM() {
        val account = vm.selectedAccount
        if (account != null) {
            accountVM = account
            accountVM?.delegate = this
            accountVM?.updateBalance()
        }
        else {
            presentWalletPicker()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            setupVM()
        }
    }

    private fun verifyWallet() {
        if (!Service.wallet.hasAccount) {
            Service.presenter.replaceWithWalletStartFragment(this)
        }
        else {
            setupVM()
        }
    }

    // DELEGATE

    override fun <M : Comparable<M>, VM : ViewModel<M>, Q : Query> didUpdateData(
            arrayViewModel: ArrayViewModel<M, VM, Q>, update: Update) {
        updateStatus()

        if (update == Update.reload) {
            selectVM()
        }
    }

    override fun <M : Comparable<M>> didUpdateData(viewModel: ViewModel<M>) {
        activity?.runOnUiThread {
            updateView()
        }
    }
}