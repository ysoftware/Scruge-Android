package com.scruge.scruge.view.ui.bounty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.hideKeyboard
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.error.GeneralError
import com.scruge.scruge.services.Service
import com.scruge.scruge.viewmodel.account.AccountAVM
import kotlinx.android.synthetic.main.fragment_bounty_submit.*

class SubmitFragment: NavigationFragment() {

    private val accountAVM = AccountAVM()
    lateinit var bountyVM: BountyVM
    lateinit var projectVM: ProjectVM

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bounty_submit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
        setupViews()
        setupActions()
    }

    private fun setupVM() {
        accountAVM.reloadData()
    }

    private fun setupActions() {
        bounty_submit_button.click {
            send()
        }
    }

    private fun setupViews() {
        title = R.string.title_submit.string()
        bounty_submit_button.title = R.string.do_send.string()
    }

    private fun send() {
        hideKeyboard()

        val providerName = projectVM.providerName
        val id = bountyVM.id ?: return alert(GeneralError.unknown)

        val vm = accountAVM.selectedAccount ?: return alert(R.string.error_wallet_not_setup.string())
        val model = vm.model ?: return alert(GeneralError.implementationError)

        val proof = bounty_submit_proof.text.toString()

        if (proof.isBlank()) {
            return alert(R.string.error_enter_proof.string())
        }

        val passcode = bounty_submit_passcode.text.toString()

        if (passcode.isEmpty()) {
            return alert(R.string.error_wallet_enter_wallet_password.string())
        }

        bounty_submit_button.isBusy = true
        Service.eos.bountySubmit(model, proof, providerName, id, passcode) { result ->
            bounty_submit_button.isBusy = false

            result.onSuccess {
                Service.api.postSubmission(id, proof, model.name, providerName) {
                    alert(R.string.alert_transaction_success.string()) {
                        navigationController?.navigateBack()
                    }
                }
            }
        }
    }
}