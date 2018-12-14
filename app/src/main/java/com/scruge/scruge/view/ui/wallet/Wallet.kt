package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.model.error.WalletError
import com.scruge.scruge.services.Service
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
    }

    override fun onResume() {
        super.onResume()
        verifyWallet()
        setupVM()
    }

    private fun setupVM() {
        vm.delegate = this
        vm.reloadData()
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
                        //                        loadingView.set(state: .error(ErrorHandler.message(for: error)))
                    }
                }
            }
            State.loading -> {
//                loadingView.set(state:.loading)
            }
            State.ready -> {
//                loadingView.set(state: .ready)
            }
            else -> { }
        }
    }

    private fun presentWalletPicker() {

    }

    private fun selectVM() {
        if (!vm.isEmpty) {
            accountVM = vm.item(0)
            accountVM?.delegate = this
            accountVM?.updateBalance()
        }

//        vm.selectedAccount?.let {
//
//            accountVM = account
//            accountVM?.delegate = self
//            accountVM?.updateBalance()
//        } ?: presentWalletPicker()
    }


    private fun verifyWallet() {
        if (!Service.wallet.hasAccount) {
            Service.presenter.replaceWithWalletStartFragment(this)
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