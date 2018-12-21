package com.scruge.scruge.view.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.formatDecimal
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
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
        contribute_button_pay.title = "PAY"
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
        title = "Invest in ${vm.title}"
    }

    private fun setupInformation() {
        vm.loadAmountContributed {
            it?.let {
                val usd = convertToUSD(it).roundToLong().toDouble().formatDecimal()
                contribute_info.text = "You have already contributed $$usd in this project"
                contribute_info.visibility = if (it != 0.0) View.VISIBLE else View.GONE
                return@loadAmountContributed
            }
            alert("Could not load information")
            navigationController?.navigateBack()
        }
        contribute_title.text = "Investing in ${vm.title}"
    }

    // METHODS

    private fun contribute() {
        if (!checked) { return }

        val account = accountVM.selectedAccount
                ?: return alert("You don't have your blockchain account set up")

        val amountSCR = amountSCR
                 ?: return alert("Enter valid contribution amount")

        val passcode = contribute_password.text.toString()

        if (passcode.isEmpty()) {
            return alert("Enter your wallet password")
        }

        vm.contribute(amountSCR, account, passcode) { error ->
            if (error != null) {
                alert(error)
            }
            else {
                alert("Transaction was successful") {
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

