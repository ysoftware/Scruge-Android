package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.hideKeyboard
import com.scruge.scruge.model.entity.Balance
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.api.Api
import com.scruge.scruge.services.eos.Token
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.account.AccountVM
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.single.ViewModel
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_staking.*

class StakeFragment: NavigationFragment(), ArrayViewModelDelegate, ViewModelDelegate {

    private val systemToken = if (Service.eos.isMainNet) Token.EOS else Token.SYS
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
        stake_button.title = "Send action"

        stake_button.click {
            accountVM.model?.let { model ->
                val cpuStr = stake_cpu_edit.text.toString()
                val cpuString = if (cpuStr.isBlank()) "0" else cpuStr
                val cpuValue = cpuString.toDoubleOrNull() ?: return@click alert("Incorrect CPU amount")
                if (cpuValue < 0.0001 && cpuValue != 0.0) {
                    return@click alert("CPU staking amount is too low")
                }

                val netStr = stake_net_edit.text.toString()
                val netString = if (netStr.isBlank()) "0" else netStr
                val netValue = netString.toDoubleOrNull() ?: return@click alert("Incorrect NET amount")
                if (netValue < 0.0001 && netValue != 0.0) {
                    return@click alert("NET staking amount is too low")
                }

                if (netValue == 0.0 && cpuValue == 0.0) {
                    return@click alert("Incorrect staking amount")
                }

                val cpu = Balance(systemToken, cpuValue)
                val net = Balance(systemToken, netValue)
                val passcode = stake_passcode.text.toString()

                hideKeyboard()
                Service.eos.stakeResources(model, cpu, net, passcode) { result ->
                    result.onSuccess {
                        alert("Transaction was successful")
                        activity?.runOnUiThread {
                            navigationController?.navigateBack()
                        }
                    }.onFailure {
                        alert(it)
                    }
                }
            }
        }
    }

    private fun setupNavigationBar() {
        (activity as? TabbarActivity)?.tabbarHidden = true
        shouldHideNavigationBar = false
        title = "Stake Resources"
    }

    private fun updateViews() {
        stake_currency.text = systemToken.symbol
        stake_currency_2.text = systemToken.symbol
        stake_resources_view.accountName = accountVM.displayName

        accountVM.name?.let {
            Service.eos.getBalance(it, listOf(systemToken)) { response ->
                activity?.runOnUiThread {
                    stake_avail.text = if (it.toString().isNotEmpty()) {
                        "${response.first()} available"
                    }
                    else {
                        "${Balance(systemToken, 0.0)} available"
                    }
                }
            }
        }
    }
}