package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.hideKeyboard
import com.scruge.scruge.model.entity.Balance
import com.scruge.scruge.model.error.EOSError
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.Settings
import com.scruge.scruge.services.eos.Token
import com.scruge.scruge.services.eos.toEosName
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.account.AccountVM
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_transfer.*
import kotlinx.android.synthetic.main.fragment_wallet.*

class TransferFragment: NavigationFragment(), ArrayViewModelDelegate, ViewModelDelegate {

    lateinit var accountVM: AccountVM
    private var balances = listOf<Balance>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transfer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        setupActions()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        setupNavigationBar()
    }

    override fun viewDidDisappear() {
        super.viewDidDisappear()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    fun setupNavigationBar() {
        (activity as? TabbarActivity)?.tabbarHidden = true
        shouldHideNavigationBar = false
        title = "Transfer tokens"
    }

    private fun setupActions() {
        transfer_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                transfer_balance_label.text = balances[position].toString()
            }
        }

        transfer_button.click {
            hideKeyboard()
            val i = transfer_spinner.selectedItemPosition

            if (balances.size <= i) {
                alert("An error occured. Select correct token to transfer")
                if (balances.isNotEmpty()) {
                    transfer_spinner.setSelection(0)
                    transfer_balance_label.text = balances[0].toString()
                }
                else {
                    navigationController?.navigateBack()
                }
                return@click
            }

            val token = balances[i].token
            val name = transfer_receiver.text.toString()
            val memo = transfer_memo.text.toString()
            val passcode = transfer_passcode.text.toString()

            val amount = transfer_amount.text.toString().toDoubleOrNull()
                ?: return@click alert("Incorrect amount value")

            val account = accountVM.model
                    ?: return@click alert("An error occured")

            val recipient = name.toEosName() ?: return@click alert(EOSError.incorrectName)

            if (amount < 0.0001) {
                return@click alert("Incorrect amount value")
            }

            if (passcode.isEmpty()) {
                return@click alert("Enter your wallet password")
            }


            val balance = Balance(token, amount)
            Service.eos.sendMoney(account, recipient, balance, memo, passcode) { result ->
                result.onSuccess {
                    alert("Transaction was successful") {
                        navigationController?.navigateBack()
                    }
                }.onFailure {
                    alert(it)
                }
            }
        }
    }

    private fun setupViews() {
        transfer_button.title = "Transfer"

        transfer_account_name.text = accountVM.displayName

        val eosName = accountVM.name ?: return
        Service.api.getDefaultTokens { result ->

            val otherTokens = result.getOrNull() ?: listOf()
            val userTokens = Service.settings.get<Set<String>>(Settings.Setting.userTokens)?.toList()
                    ?.mapNotNull { Token.from(it) } ?: listOf()
            val list = Token.default + userTokens + otherTokens

            Service.eos.getBalance(eosName, list, requestAll = true) { response ->
                balances = response.filter { it.amount != 0.0 }.distinct()

                if (balances.isEmpty()) {
                    alert("Could not load your information")
                    return@getBalance
                }

                val context = activity ?: return@getBalance

                activity?.runOnUiThread {
                    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item,
                                               balances.map { it.token.symbol })
                    transfer_spinner.adapter = adapter

                    // select eos or first element
                    var i = balances.indexOfFirst { it.token == Service.eos.systemToken }
                    if (i == -1) {
                        i = 0
                    }
                    transfer_spinner.setSelection(i)
                    transfer_balance_label.text = balances[i].toString()
                }
            }
        }
    }
}