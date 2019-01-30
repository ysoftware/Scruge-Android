package com.scruge.scruge.view.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.VoteInfo
import com.scruge.scruge.model.entity.VoteKind
import com.scruge.scruge.view.cells.*
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.view.ui.campaign.CampaignFragment
import com.scruge.scruge.viewmodel.account.AccountAVM
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import com.scruge.scruge.viewmodel.update.UpdateVM
import com.ysoftware.mvvm.single.ViewModel
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_vote.*

class VoteFragment: NavigationFragment(), ViewModelDelegate {

    enum class Block(val rawValue:Int) {
        info(0),  update(1), milestone(2),
        countdown(3), controls(4);

        companion object {
            fun from(rawValue: Int):Block {
                return when (rawValue) {
                    0 -> info
                    1 -> update
                    2 -> milestone
                    3 -> countdown
                    4 -> controls
                    else -> info
                }
            }
        }
    }

    // PROPERTIES

    lateinit var vm: CampaignVM
    private var voting: VoteInfo? = null
    private val accountVM = AccountAVM()
    private var updateVM = UpdateVM()
    private var adapter = Adapter(this)

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vote, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
        setupTable()
        loadVote()
    }

    override fun viewDidAppear() {
        setupNavigationBar()
    }

    private fun setupTable() {
        vote_recycler_view.setupForVerticalLayout()
        vote_recycler_view.adapter = adapter
    }

    private fun setupVM() {
        vm.load()
        accountVM.reloadData()
    }

    private fun setupNavigationBar() {
        (activity as? TabbarActivity)?.tabbarHidden = true
        shouldHideNavigationBar = false
        title = R.string.title_vote.string()
    }

    private fun loadVote() {
        vm.loadVoteInfo { voting ->
            this.voting = voting
            adapter.notifyDataSetChanged()
        }
    }

    override fun <M : Comparable<M>> didUpdateData(viewModel: ViewModel<M>) {
        super.didUpdateData(viewModel)
        adapter.notifyDataSetChanged()
    }

    private fun vote(value:Boolean, passcode:String) {
        val account = accountVM.selectedAccount ?: return alert(R.string.error_wallet_not_setup.string())

        if (passcode.isEmpty()) {
            return alert(R.string.error_wallet_enter_wallet_password.string())
        }

        vm.vote(value, account, passcode) { error ->
            if (error != null) {
                alert(error)
            }
            else {
                alert(R.string.alert_transaction_success.string()) { navigationController?.navigateBack() }
            }
        }
    }

    // ADAPTER

    class Adapter(private val fr:VoteFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            return CampaignFragment.Block.values()[position].rawValue
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val i = LayoutInflater.from(parent.context)
            val block = Block.from(viewType)
            return when (block) {
                Block.info -> VoteInfoCell(i.inflate(R.layout.cell_vote_info, parent, false))
                Block.milestone -> PagingCell(i.inflate(R.layout.cell_paging, parent, false))
                Block.update -> LastUpdateCell(i.inflate(R.layout.cell_update_last, parent, false))
                Block.countdown -> CountdownCell(i.inflate(R.layout.cell_countdown, parent, false))
                Block.controls -> VoteControlsCell(i.inflate(R.layout.cell_controls, parent, false))
            }
        }

        override fun getItemCount(): Int {
            return Block.values().size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val block = Block.from(position)
            when (block) {
                Block.info -> (holder as? VoteInfoCell)?.setup(fr.vm, VoteKind.from(fr.voting?.kind ?: 0))
                Block.milestone -> fr.vm.milestonesVM?.let { vm ->
                    fr.vm.currentMilestoneVM?.let { cvm ->
                        (holder as? PagingCell)?.setup(vm, cvm)
                                ?.milestoneTap { milestoneVM ->

                                }
                    }
                }
                Block.update -> fr.vm.lastUpdateVM?.let {
                    (holder as? LastUpdateCell)?.setup(it, R.string.title_rationale.string())
                            ?.updateTap { update ->

                            }
                            ?.allUpdatesTap {

                            }
                }
                Block.countdown ->
                    (holder as? CountdownCell)?.setup(R.string.title_vote_ends_in.string(),
                                                      fr.voting?.endTimestamp ?: 0)
                Block.controls ->
                    (holder as? VoteControlsCell)?.setup { passcode, value ->
                        fr.vote(value, passcode)
                    }
            }
        }
    }
}