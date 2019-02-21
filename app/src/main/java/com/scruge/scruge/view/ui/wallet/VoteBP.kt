package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.hideKeyboard
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.Producer
import com.scruge.scruge.model.error.WalletError
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.eos.EosName
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.account.AccountVM
import com.scruge.scruge.viewmodel.producer.ProducerVM
import kotlinx.android.synthetic.main.cell_bp.view.*
import kotlinx.android.synthetic.main.fragment_vote_bp.*

class VoteBPFragment: NavigationFragment() {

    // PROPERTIES

    lateinit var accountVM: AccountVM

    private val adapter = Adapter(this)
    private var all:MutableSet<ProducerVM> = mutableSetOf()
    private var selected:MutableSet<ProducerVM> = mutableSetOf()
    private var filtered:MutableSet<ProducerVM> = mutableSetOf()

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vote_bp, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        setupActions()
        updateSelectedCount()
        loadList()
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

    private fun setupNavigationBar() {
        (activity as? TabbarActivity)?.tabbarHidden = true
        shouldHideNavigationBar = false
        title = R.string.title_transfer_tokens.string()
    }

    private fun setupActions() {
        bp_vote_button.click { send() }

        bp_filter_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val s = s.toString()
                if (s.isBlank()) {
                    filtered = all
                }
                else {
                    filtered = all.filter { it.name.contains(s.toLowerCase()) }.toMutableSet()
                }
                adapter.notifyDataSetChanged()
                updateSelectedCount()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupViews() {
        bp_recycler.setupForVerticalLayout()
        bp_recycler.adapter = adapter
        bp_vote_button.title = R.string.do_vote_bp.string()
    }

    private fun send() {
        hideKeyboard()

        val account = accountVM.model
                ?: return alert(WalletError.unknown)

        val passcode = bp_password_field.text.toString()

        if (passcode.isEmpty()) {
            return alert(R.string.error_wallet_enter_wallet_password.string())
        }

        bp_vote_button.isBusy = true
        Service.eos.voteProducers(account,
                                  selected.mapNotNull { EosName.from(it.name) }.toSet(),
                                  passcode) { result ->
            bp_vote_button.isBusy = false

            result.onSuccess {
                alert(R.string.alert_transaction_success.string()) {
                    navigationController?.navigateBack()
                }
            }.onFailure {
                alert(it)
            }
        }
    }

    // METHODS

    private fun loadList() {
        Service.eos.getProducers { result ->
            activity?.runOnUiThread {
                result.onSuccess { info ->
                    all = info.rows.map { ProducerVM(Producer(it), info.total_producer_vote_weight) }
                            .toMutableSet()
                    filtered = all
                    adapter.notifyDataSetChanged()
                }.onFailure { error ->
                    alert(error) {
                        navigationController?.navigateBack()
                    }
                }
            }
        }
    }

    private fun updateSelectedCount() {
        bp_selected_label.text = R.string.label_selected_bps.string(selected.count().toString())
    }

    // ADAPTER

    class Adapter(val fr:VoteBPFragment): RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder {
            return Adapter.ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.cell_bp, parent, false))
        }

        override fun getItemCount(): Int {
            return fr.filtered.size
        }

        override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
            val vm = fr.filtered.elementAt(position)
            holder.setup(vm, fr.selected.contains(vm))

            holder.itemView.setOnClickListener {
                if (!holder.isSelected) {
                    fr.selected.add(vm)
                }
                else {
                    fr.selected.remove(vm)
                }
                notifyDataSetChanged()
                fr.updateSelectedCount()
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var isSelected = false

            fun setup(vm: ProducerVM, selected: Boolean) {
                isSelected = selected

                itemView.bp_name.text = vm.name
                itemView.bp_votes.text = vm.votes

                itemView.bp_checkmark_image.visibility = if (selected) View.VISIBLE else View.GONE
                itemView.bp_checkmark.setBackgroundResource(
                        if (selected) R.drawable.checkmark_selected else R.drawable.checkmark_unselected)
            }
        }
    }
}