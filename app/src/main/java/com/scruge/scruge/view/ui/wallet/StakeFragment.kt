package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.hideKeyboard
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.Balance
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.view.views.ButtonView
import com.scruge.scruge.viewmodel.account.AccountVM
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_staking.*

class StakeFragment: NavigationFragment(), ArrayViewModelDelegate, ViewModelDelegate {

    lateinit var accountVM: AccountVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_staking, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setupNavigationBar()
        updateViews()
    }

    override fun viewDidDisappear() {
        super.viewDidDisappear()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    private fun setupViews() {
        stake_resources_view.hideControls(true)
        stake_resources_view.hideRAM(true)
        stake_button.title = R.string.do_stake.string()
        unstake_button.title = R.string.do_unstake_resources.string()

        val listener: (ButtonView)->Unit = { sender ->
            accountVM.model?.let { model ->
                val cpuStr = stake_cpu_edit.text.toString()
                val cpuString = if (cpuStr.isBlank()) "0" else cpuStr
                val cpuValue = cpuString.toDoubleOrNull()
                        ?: return@let alert(R.string.error_wallet_invalid_amount.string())
                if (cpuValue < 0.0001 && cpuValue != 0.0) {
                    return@let alert(R.string.error_wallet_invalid_amount.string())
                }

                val netStr = stake_net_edit.text.toString()
                val netString = if (netStr.isBlank()) "0" else netStr
                val netValue = netString.toDoubleOrNull()
                        ?: return@let alert(R.string.error_wallet_invalid_amount.string())
                if (netValue < 0.0001 && netValue != 0.0) {
                    return@let alert(R.string.error_wallet_invalid_amount.string())
                }

                if (netValue == 0.0 && cpuValue == 0.0) {
                    return@let alert(R.string.error_wallet_invalid_amount.string())
                }

                val systemToken = Service.eos.systemToken
                val cpu = Balance(systemToken, cpuValue)
                val net = Balance(systemToken, netValue)
                val passcode = stake_passcode.text.toString()

                if (passcode.isEmpty()) {
                    return@let alert(R.string.error_wallet_enter_wallet_password.string())
                }

                hideKeyboard()

                val block:(Result<String>)->Unit = { result ->
                    result.onSuccess {
                        alert(R.string.alert_transaction_success.string())
                        activity?.runOnUiThread {
                            navigationController?.navigateBack()
                        }
                    }.onFailure {
                        alert(it)
                    }
                }

                if (sender == stake_button) {
                    Service.eos.stakeResources(model, cpu, net, passcode, block)
                }
                else {
                    Service.eos.unstakeResources(model, cpu, net, passcode, block)
                }
            }
        }
        stake_button.click { listener(it) }
        unstake_button.click { listener(it) }
    }

    private fun setupNavigationBar() {
        (activity as? TabbarActivity)?.tabbarHidden = true
        shouldHideNavigationBar = false
        title = R.string.title_manage_resources.string()
    }

    private fun updateViews() {
        val systemToken = Service.eos.systemToken
        stake_currency.text = systemToken.symbol
        stake_currency_2.text = systemToken.symbol
        stake_resources_view.accountName = accountVM.name

        accountVM.name?.let {
            Service.eos.getBalance(it, listOf(systemToken)) { response ->
                activity?.runOnUiThread {
                    val tokens = if (it.toString().isNotEmpty()) response.first().toString()
                    else Balance(systemToken, 0.0).toString()
                    stake_avail.text = R.string.tokens_available.string(tokens)
                }
            }
        }
    }
}