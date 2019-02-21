package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.Balance
import com.scruge.scruge.model.error.GeneralError
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.eos.Token
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.account.AccountVM
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_buyram.*
import kotlin.math.roundToInt

class BuyRAMFragment: NavigationFragment(), ArrayViewModelDelegate, ViewModelDelegate {

    private enum class Action {
        buyBytes, buyEOS, sell;

        val label:String get() {
            return when (this) {
                sell -> R.string.label_sell_ram.string()
                buyBytes -> R.string.label_buy_ram_bytes.string()
                buyEOS -> R.string.label_buy_ram_eos.string()
            }
        }
    }

    // PROPERTIES

    lateinit var accountVM: AccountVM
    private var action = Action.buyBytes
    private var price = 0.0

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_buyram, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        setupActions()
        updateViews()
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
        shouldHideNavigationBar = true
        title = R.string.title_buy_ram.string()
    }

    private fun setupViews() {
        val context = activity ?: return
        ram_resources.accountName = accountVM.name
        ram_resources.hideControls(true)
        ram_resources.hideResources(true)

        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item,
                                   Action.values().map { it.label })
        ram_spinner.adapter = adapter
        ram_spinner.setSelection(0)
        ram_price.text = Action.values().first().label

        ram_amount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                updateViews(s.toString())
            }
        })

        Service.eos.getRamPrice { result ->
            activity?.runOnUiThread {
                result.onSuccess {
                    this.price = it
                    val price = (it * 1024).formatRounding(4, 4)
                    ram_price.text = R.string.current_ram_price.string(price)
                }.onFailure {
                    alert(it)
                }
                updateViews()
            }
        }
    }

    private fun setupActions() {
        ram_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                action = Action.values()[position]
                updateViews()
            }
        }
        ram_button.click { send() }
    }

    private fun updateViews(text:String = "") {
        val input = text.toDoubleOrNull() ?: 0.0

        when (action) {
            Action.sell -> {
                val value = (price * input.roundToInt()).formatRounding(4, 4)
                ram_button.title = R.string.do_sell_eos_ram.string(value)
            }
            Action.buyBytes -> {
                val value = (price * input.roundToInt()).formatRounding(4, 4)
                ram_button.title = R.string.do_buy_bytes_ram.string(value)
            }
            Action.buyEOS -> {
                val value = (input / price).formatRounding(0, 0)
                ram_button.title = R.string.do_buy_eos_ram.string(value)
            }
        }
    }

    private fun send() {
        val model = accountVM.model ?: return alert(GeneralError.unknown)

        val input = ram_amount.text.toString().toDoubleOrNull()
                ?: return alert(R.string.error_incorrect_input.string())

        val passcode = ram_passcode.text.toString()

        if (passcode.isEmpty()) {
            return alert(R.string.error_wallet_enter_wallet_password.string())
        }

        ram_button.isBusy = true

        val block:(Result<String>)->Unit = {
            ram_button.isBusy = false

            it.onSuccess {
                alert(R.string.alert_transaction_success.string()) {
                    navigationController?.navigateBack()
                }
            }.onFailure {
                alert(it)
            }
        }

        when (action) {
            Action.sell -> Service.eos.sellRam(model, input.toLong(), passcode, block)
            Action.buyBytes -> Service.eos.buyRam(model, input.toLong(), passcode, block)
            Action.buyEOS -> {
                val amount = Balance(Token.EOS, input)
                Service.eos.buyRam(model, amount, passcode, block)
            }
        }
    }
}