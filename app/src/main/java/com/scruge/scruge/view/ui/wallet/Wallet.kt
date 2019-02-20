package com.scruge.scruge.view.ui.wallet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.ask
import com.scruge.scruge.dependencies.view.setHidden
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.dependencies.view.toggleHidden
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
import kotlinx.android.synthetic.main.view_wallet_settings.view.*

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

        verifyWallet()
        setupNavigationBar()
        setupVM()
    }

    override fun onPause() {
        super.onPause()
        wallet_data_view?.lock()
    }

    override fun viewDidDisappear() {
        super.viewDidDisappear()
        wallet_data_view?.lock()
    }

    private fun setupVM() {
        vm.delegate = this
        vm.reloadData()
        collapseAll()
    }

    private fun setupNavigationBar() {
        (activity as? TabbarActivity)?.tabbarHidden = false
        shouldHideNavigationBar = true
    }

    private fun collapseAll(view:View? = null) {
        listOf(wallet_data_view,
               wallet_transactions_view,
               wallet_resources_view).forEach { if (view != it) it.setHidden(true) }
    }

    private fun setupActions() {
        wallet_settings.setOnClickListener {
            val activity = activity ?: return@setOnClickListener

            val dialog = BottomSheetDialog(activity, R.style.BottomSheetDialog)
            val menu = LayoutInflater.from(activity).inflate(R.layout.view_wallet_settings, null)
            if (vm.numberOfItems == 1) {
                menu.wallet_settings_switch.visibility = View.GONE
            }
            else {
                menu.wallet_settings_switch.setOnClickListener {
                    dialog.hide()
                    presentWalletPicker()
                }
            }

            menu.wallet_settings_delete.setOnClickListener {
                dialog.hide()
                deleteWallet()
            }

            dialog.setContentView(menu)
            dialog.show()
        }

        wallet_data_container.setOnClickListener {
            collapseAll(wallet_data_view)
            if (!wallet_data_view.toggleHidden()) {
                scrollToBottom()
                wallet_data_view.updateViews()
            }
            else {
                wallet_data_view.lock()
            }
        }

        wallet_transactions_container.setOnClickListener {
            collapseAll(wallet_transactions_view)
            if (!wallet_transactions_view.toggleHidden()) {
                scrollToBottom()
                wallet_transactions_view.accountName = vm.selectedAccount?.name
            }
        }

        wallet_resources_container.setOnClickListener {
            collapseAll(wallet_resources_view)
            if (!wallet_resources_view.toggleHidden()) {
                scrollToBottom()
                wallet_resources_view.accountName = vm.selectedAccount?.name
            }
        }

        wallet_resources_view.buyRamBlock = {
            accountVM?.let {
                Service.presenter.presentBuyRAMFragment(this, it)
            }
        }

        wallet_resources_view.stakeBlock = {
            accountVM?.let {
                Service.presenter.presentStakeFragment(this, it)
            }
        }

        wallet_transfer.setOnClickListener {
            accountVM?.let {
                Service.presenter.presentTransferFragment(this, it)
            }
        }

        wallet_vote_bp.setOnClickListener {
            accountVM?.let {
                Service.presenter.presentVoteBPFragment(this, it)
            }
        }
    }

    private fun updateView() {
        wallet_account_name.text = accountVM?.displayName ?: ""
        wallet_balance.text = accountVM?.balanceString() ?: ""
    }

    private fun updateStatus() {
        when (vm.state) {
            State.error -> {
                val error = ErrorHandler.error(vm.state.errorValue)
                Handler(Looper.getMainLooper()).postDelayed({
                    when (error) {
                        WalletError.noKey -> Service.presenter.replaceWithWalletStartFragment(this)
                        WalletError.noAccounts -> Service.presenter.replaceWithWalletNoAccountFragment(this)
                        else -> {
                            val e = ViewState.error
                            e.errorMessage = ErrorHandler.message(error)
                            wallet_loading_view?.state = e
                        }
                    }
                }, 500)
            }
            State.loading -> {
                wallet_loading_view?.state = ViewState.loading
            }
            State.ready -> {
                wallet_loading_view?.state = ViewState.ready
            }
            else -> {
            }
        }
    }

    private fun deleteWallet() {
        val t = R.string.title_sure_to_delete_wallet.string()
        val q = R.string.label_sure_to_delete_wallet.string()

        ask(t, q) { r ->
            if (r) {
                vm.deleteWallet()
                Service.presenter.replaceWithWalletStartFragment(this)
            }
        }
    }

    private fun presentWalletPicker() {
        if (vm.isEmpty()) { return }

        if (vm.numberOfItems == 1) {
            Service.settings.set(Settings.Setting.selectedAccount, vm.item(0).displayName)
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
            collapseAll()
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

    private fun scrollToBottom() {
        Handler().postDelayed(
                { activity?.runOnUiThread { wallet_scroll_view.fullScroll(View.FOCUS_DOWN) } }, 250)
    }

    // DELEGATE

    override fun <M : Comparable<M>, VM : ViewModel<M>, Q : Query> didUpdateData(
            arrayViewModel: ArrayViewModel<M, VM, Q>, update: Update) {

        if (update == Update.reload) {
            activity?.runOnUiThread {
                selectVM()
            }
        }
    }

    override fun didChangeState(state: State) {
        updateStatus()
    }

    override fun <M : Comparable<M>> didUpdateData(viewModel: ViewModel<M>) {
        activity?.runOnUiThread {
            updateView()
        }
    }
}