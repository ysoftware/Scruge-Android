package com.scruge.scruge.view.ui.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.model.ViewState
import com.scruge.scruge.view.cells.*
import com.scruge.scruge.view.ui.campaign.CampaignFragment.Block.*
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import com.scruge.scruge.viewmodel.faq.FaqVM
import com.ysoftware.mvvm.array.ArrayViewModel
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.array.Query
import com.ysoftware.mvvm.array.Update
import com.ysoftware.mvvm.single.ViewModel
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_campaign.*

class CampaignFragment: NavigationFragment(), ViewModelDelegate, ArrayViewModelDelegate {

    enum class Block(val rawValue:Int) {
        info(0),  economies(1), update(2), comments(3),
        about(4), faq(5), milestone(6), documents(7);

        companion object {
            fun from(rawValue: Int):Block {
                return when (rawValue) {
                    0 -> info
                    1 -> economies
                    2 -> update
                    3 -> comments
                    4 -> about
                    5 -> faq
                    6 -> milestone
                    7 -> documents
                    else -> info
                }
            }
        }
    }

    // PROPERTIES

    lateinit var vm: CampaignVM
    private var adapter:Adapter? = null

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_campaign, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
        setupTable()
    }

    private fun setupVM() {
        vm.delegate = this
        vm.load()
        adapter = Adapter(this)
    }

    private fun setupTable() {
        recycler_view.setupForVerticalLayout()
        recycler_view.adapter = adapter
    }

    private fun setupNavigationBar() {
        // todo subscribe button
    }

    private fun setupBottomButton() {
        // todo invest and vote
    }

    fun shouldDisplay(block:Block):Boolean {
        return when (block) {
            info, comments, about -> true
            milestone -> vm.currentMilestoneVM != null
            update -> vm.lastUpdateVM != null
            economies -> vm.economiesVM != null
            documents -> vm.documentsVM?.numberOfItems ?: 0 != 0
            faq -> vm.faqVM?.numberOfItems ?: 0 != 0
        }
    }

    private fun showData() {
        vm.lastUpdateVM?.delegate = this
        vm.currentMilestoneVM?.delegate = this
        vm.topCommentsVM?.delegate = this
        vm.economiesVM?.delegate = this
        vm.milestonesVM?.delegate = this

        adapter?.notifyDataSetChanged()
        setupBottomButton()
    }

    // DELEGATE

    override fun <M : Comparable<M>> didUpdateData(viewModel: ViewModel<M>) {
        if (viewModel !== vm) {
            adapter?.notifyDataSetChanged()
            return
        }

        campaign_loading_view.state = vm.state

        // todo
        when (vm.state) {
            ViewState.error -> {
                //                refresh_control.endRefreshing
            }
            ViewState.ready -> {
                showData()
                //                refresh_control.endRefreshing
            }
            ViewState.loading -> { }
        }
    }

    override fun <M : Comparable<M>, VM : ViewModel<M>, Q : Query> didUpdateData(
            arrayViewModel: ArrayViewModel<M, VM, Q>, update: Update) {
        adapter?.notifyDataSetChanged()
        setupNavigationBar()
    }

    // ADAPTER

    class Adapter(private val fr:CampaignFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            return values().filter { fr.shouldDisplay(it) }[position].rawValue
        }

        override fun getItemCount(): Int {
            return values().filter { fr.shouldDisplay(it) }.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val i = LayoutInflater.from(parent.context)
            val block = Block.from(viewType)
            return when (block) {
                about -> AboutCell(i.inflate(R.layout.cell_about, parent, false))
                info -> CampaignCell(i.inflate(R.layout.cell_campaign_info, parent, false))
                economies -> EconomiesCell(i.inflate(R.layout.cell_economies, parent, false))
                faq -> FaqCell(i.inflate(R.layout.cell_faq, parent, false))
                milestone -> PagingCell(i.inflate(R.layout.cell_paging, parent, false))
                update -> LastUpdateCell(i.inflate(R.layout.cell_paging, parent, false))
                comments -> TopCommentCell(i.inflate(R.layout.cell_comment_top, parent, false))
                documents -> DocumentCell(i.inflate(R.layout.cell_documents, parent, false))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val block = Block.from(position)
            when (block) {
                about -> (holder as? AboutCell)?.setup(fr.vm)
                            ?.memberTap { member ->

                            }?.socialTap { social ->

                            }
                info -> (holder as? CampaignCell)?.setup(fr.vm)
                economies -> fr.vm.economiesVM?.let {
                        (holder as? EconomiesCell)?.setup(it)
                    }
                faq -> fr.vm.faqVM?.let {
                        (holder as? PagingCell)?.setup(it)
                                ?.tap { faqVM ->

                                }
                    }
                milestone -> fr.vm.milestonesVM?.let { vm ->
                        fr.vm.currentMilestoneVM?.let { cvm ->
                            (holder as? PagingCell)?.setup(vm, cvm)
                                    ?.tap { milestoneVM ->

                            }
                        }
                    }
                update -> fr.vm.lastUpdateVM?.let {
                        (holder as? LastUpdateCell)?.setup(it)
                                ?.updateTap { update ->

                                }
                                ?.allUpdatesTap {

                                }
                    }
                comments -> (holder as? TopCommentCell)?.setup(fr.vm.topCommentsVM, fr.vm.commentsCount)
                        ?.allComments {

                        }
                documents -> fr.vm.documentsVM?.let {
                            (holder as? DocumentsCell)?.setup(it)
                                    ?.tap { doc ->

                                    }
                        }
            }
        }
    }
}