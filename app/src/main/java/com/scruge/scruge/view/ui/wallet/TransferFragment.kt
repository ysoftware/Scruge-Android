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
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.Balance
import com.scruge.scruge.model.error.EOSError
import com.scruge.scruge.model.error.WalletError
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.Settings
import com.scruge.scruge.services.eos.Token
import com.scruge.scruge.services.eos.toEosName
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.account.AccountVM
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_transfer.*

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
        title = R.string.title_transfer_tokens.string()
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
                alert(R.string.alert_select_correct_token.string())
                if (balances.isNotEmpty()) {
                    transfer_spinner.setSelection(0)
                    transfer_balance_label.text = balances[0].toString()
                }
                else {
                    navigationController?.navigateBack()
                }
                return@click
            }

            if (balances.isEmpty()) return@click

            val token = balances[i].token
            val name = transfer_receiver.text.toString()
            val memo = transfer_memo.text.toString()
            val passcode = transfer_passcode.text.toString()

            val amount = transfer_amount.text.toString().toDoubleOrNull()
                ?: return@click alert(EOSError.incorrectToken)

            val account = accountVM.model
                    ?: return@click alert(WalletError.unknown)

            val recipient = name.toEosName() ?: return@click alert(EOSError.incorrectName)

            if (amount < 0.0001) {
                return@click alert(R.string.error_wallet_invalid_amount.string())
            }

            if (passcode.isEmpty()) {
                return@click alert(R.string.error_wallet_enter_wallet_password.string())
            }


            val balance = Balance(token, amount)
            Service.eos.sendMoney(account, recipient, balance, memo, passcode) { result ->
                result.onSuccess {
                    alert(R.string.alert_transaction_success.string()) {
                        navigationController?.navigateBack()
                    }
                }.onFailure {
                    alert(it)
                }
            }
        }
    }

    private fun setupViews() {
        transfer_button.title = R.string.do_transfer_tokens.string()

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
                    alert(R.string.error_wallet_no_transferable_tokens.string()) {
                        navigationController?.navigateBack()
                    }
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