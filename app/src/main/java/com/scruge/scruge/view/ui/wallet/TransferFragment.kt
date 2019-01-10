package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.model.entity.Balance
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.Settings
import com.scruge.scruge.services.eos.Token
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.account.AccountVM
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_transfer.*
import kotlinx.android.synthetic.main.fragment_wallet.*

class TransferFragment: NavigationFragment(), ArrayViewModelDelegate, ViewModelDelegate {

    var accountVM: AccountVM? = null
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

        setupNavigationBar()
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
    }

    private fun setupViews() {
        val account = accountVM?.name ?: return
        Service.api.getDefaultTokens { result ->

            val otherTokens = result.getOrNull() ?: listOf()
            val userTokens =
                    Service.settings.get<Set<String>>(Settings.Setting.userTokens)?.map { Token(it) } ?: listOf()
            val list = Token.default + userTokens.toList() + otherTokens

            Service.eos.getBalance(account, list, requestAll = true) { response ->
                balances = response.filter { it.amount != 0.0 }.distinct()

                if (balances.isEmpty()) {
                    alert("This account has no tokens available to transfer")
                    navigationController?.navigateBack()
                    return@getBalance
                }

                activity?.runOnUiThread {

                    // select eos or first element
                    var i = balances.indexOfFirst { it.token == Token.EOS }
                    if (i == -1) { i = 0 }
                    transfer_spinner.setSelection(i)
                    transfer_balance_label.text = balances[i].toString()

                    val context = activity ?: return@runOnUiThread
                    val adapter = ArrayAdapter(context,
                                               android.R.layout.simple_spinner_dropdown_item,
                                               balances.map { it.token.symbol })
                    transfer_spinner.adapter = adapter
                }
            }
        }
    }
}