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
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.api.Api
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.account.AccountVM
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.single.ViewModel
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_staking.*

class StakeFragment: NavigationFragment(), ArrayViewModelDelegate, ViewModelDelegate {

    private val systemToken = if (Service.eos.isMainNet) "EOS" else "SYS"
    var accountVM: AccountVM? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_staking, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        setupVM()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()

        setupNavigationBar()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun viewDidDisappear() {
        super.viewDidDisappear()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    private fun setupViews() {
        stake_resources_view.hideControls(true)
        stake_resources_view.hideRAM(true)
        stake_button.title = "Send"

        stake_button.click {
            accountVM?.model?.let { model ->
                val cpu = stake_cpu_edit.text.toString().toDoubleOrNull()?.formatRounding(4, 4, ".") ?:
                        return@let alert("Incorrect input")
                val net = stake_net_edit.text.toString().toDoubleOrNull()?.formatRounding(4, 4, ".") ?:
                        return@let alert("Incorrect input")

                val passcode = stake_passcode.text.toString()

                Service.eos.stakeResources(model, "$cpu $systemToken", "$net $systemToken", passcode) { result ->
                    result.onSuccess {
                        alert(it)
                        navigationController?.navigateBack()
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

    private fun setupVM() {
        stake_resources_view.accountName = accountVM?.name
        accountVM?.delegate = this
        accountVM?.updateBalance()
    }

    override fun <M : Comparable<M>> didUpdateData(viewModel: ViewModel<M>) {

        activity?.runOnUiThread {
            updateViews()
        }
    }

    private fun updateViews() {
        val eosBalance = accountVM?.balanceString(currency = systemToken) ?: "$systemToken 0.0000"
        stake_avail.text = "${eosBalance} available"
        stake_currency.text = systemToken
        stake_currency_2.text = systemToken
    }
}