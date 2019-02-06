package com.scruge.scruge.view.ui.details

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.VoteKind
import com.scruge.scruge.model.entity.VoteResult
import com.scruge.scruge.view.cells.CountdownCell
import com.scruge.scruge.view.cells.VoteInfoCell
import com.scruge.scruge.view.cells.VoteResultCell
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.view.ui.campaign.CampaignFragment
import com.scruge.scruge.viewmodel.account.AccountAVM
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import com.scruge.scruge.viewmodel.update.UpdateVM
import com.ysoftware.mvvm.single.ViewModel
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_vote.*

class VoteResultFragment: NavigationFragment(), ViewModelDelegate {

    enum class Block(val rawValue:Int) {
        info(0),  result(1), countdown(2);

        companion object {
            fun from(rawValue: Int):Block {
                return when (rawValue) {
                    0 -> info
                    1 -> result
                    2 -> countdown
                    else -> info
                }
            }
        }
    }

    // PROPERTIES

    lateinit var vm: CampaignVM
    private var voteResult: VoteResult? = null
    private val accountVM = AccountAVM()
    private var updateVM = UpdateVM()
    private var adapter = Adapter(this)

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_vote_results, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
        setupTable()
        loadResult()
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
        title = R.string.title_voting_progress.string()
    }

    private fun loadResult() {
        vm.loadVoteResults { voting ->
            this.voteResult = voting
            adapter.notifyDataSetChanged()
        }
    }

    override fun <M : Comparable<M>> didUpdateData(viewModel: ViewModel<M>) {
        super.didUpdateData(viewModel)
        activity?.runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }

    // ADAPTER

    class Adapter(private val fr:VoteResultFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            return CampaignFragment.Block.values()[position].rawValue
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val i = LayoutInflater.from(parent.context)
            val block = Block.from(viewType)
            return when (block) {
                Block.info -> VoteInfoCell(i.inflate(R.layout.cell_vote_info, parent, false))
                Block.countdown -> CountdownCell(i.inflate(R.layout.cell_countdown, parent, false))
                Block.result -> VoteResultCell(i.inflate(R.layout.cell_vote_result, parent, false))
            }
        }

        override fun getItemCount(): Int {
            return Block.values().size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val block = Block.from(position)
            when (block) {
                Block.info -> (holder as? VoteInfoCell)?.setup(fr.vm, VoteKind.from(fr.voteResult?.kind ?: 0))
                Block.countdown ->
                    (holder as? CountdownCell)?.setup(R.string.title_vote_ends_in.string(),
                                                      fr.voteResult?.endTimestamp ?: 0)
                Block.result -> (holder as? VoteResultCell)?.setup(fr.voteResult)
            }
        }
    }
}
