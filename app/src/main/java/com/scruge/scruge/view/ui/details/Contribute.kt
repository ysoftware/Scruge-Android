package com.scruge.scruge.view.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.formatDecimal
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.view.views.ButtonView
import com.scruge.scruge.viewmodel.account.AccountAVM
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import kotlinx.android.synthetic.main.fragment_contribute.*
import kotlin.math.roundToLong

class ContributeFragment : NavigationFragment() {

    private val conversionRate = 1.5 // $ for 1 SCR

    lateinit var vm: CampaignVM
    private val accountVM = AccountAVM()
    private var checked = false
    private val amountSCR get() = contribute_usd.text.toString().toDoubleOrNull()?.let { convertToUSD(it) }

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contribute, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
        setupActions()
        setupInformation()
        setupButton()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()
        setupNavigationBar()
    }

    private fun setupButton() {
        contribute_button_pay.title = R.string.do_pay_cap.string()
        contribute_button_pay.color = ButtonView.Color.gray
    }

    private fun setupActions() {
        contribute_checkmark_view.setOnClickListener {
            checked = !checked
            contribute_checkmark_selected.visibility = if (checked) View.VISIBLE else View.GONE
            contribute_button_pay.color = if (checked) ButtonView.Color.purple else ButtonView.Color.gray
        }

        contribute_button_pay.click { contribute() }
    }

    private fun setupVM() {
        accountVM.reloadData()
    }

    private fun setupNavigationBar() {
        shouldHideNavigationBar = false
        title = R.string.do_invest_in.string(vm.title)
    }

    private fun setupInformation() {
        vm.loadAmountContributed {
            it?.let {
                val usd = convertToUSD(it).roundToLong().toDouble().formatDecimal(" ")
                contribute_info.text = R.string.label_you_have_contributed_usd.string(usd)
                contribute_info.visibility = if (it != 0.0) View.VISIBLE else View.GONE
                return@loadAmountContributed
            } ?: alert(R.string.error_wallet_no_transferable_tokens.string())
        }
        contribute_title.text = R.string.label_investing_in.string(vm.title)
    }

    // METHODS

    private fun contribute() {
        if (!checked) { return }

        val account = accountVM.selectedAccount
                ?: return alert(R.string.error_wallet_not_setup.string())

        val amountSCR = amountSCR
                 ?: return alert(R.string.error_wallet_invalid_amount.string())

        val passcode = contribute_password.text.toString()

        if (passcode.isEmpty()) {
            return alert(R.string.error_wallet_enter_wallet_password.string())
        }

        vm.contribute(amountSCR, account, passcode) { error ->
            if (error != null) {
                alert(error)
            }
            else {
                alert(R.string.alert_transaction_success.string()) {
                    navigationController?.navigateBack()
                }
            }
        }
    }

    private fun convertToSCR(amount: Double): Double {
        return amount * conversionRate
    }

    private fun convertToUSD(amount: Double): Double {
        return amount / conversionRate
    }
}

