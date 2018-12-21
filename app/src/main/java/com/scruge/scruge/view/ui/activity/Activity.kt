package com.scruge.scruge.view.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.model.ViewState
import com.scruge.scruge.model.entity.Voting
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.cells.ActivityViewHolder
import com.scruge.scruge.view.cells.VoteNotificationCell
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.update.UpdateAVM
import com.ysoftware.mvvm.array.*
import com.ysoftware.mvvm.single.ViewModel
import kotlinx.android.synthetic.main.fragment_activity.*

class ActivityFragment: NavigationFragment(), ArrayViewModelDelegate {

    // PROPERTIES

    private var activeVoting:List<Voting> = listOf()
    private val vm = UpdateAVM(UpdateAVM.Source.activity)
    private val adapter = Adapter(this)
    private val handler = ArrayViewModelUpdateHandler(adapter)

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = "Activity"
        setupVM()
        setupTable()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()

        (activity as? TabbarActivity)?.tabbarHidden = false
        when (vm.state) {
            State.error, State.ready -> {
                if (vm.isEmpty()) {
                    vm.reloadData()
                }
            }
        }
    }

    private fun setupVM() {
        vm.delegate = this
        vm.reloadData()
    }

    private fun setupTable() {
        refresh_control.setOnRefreshListener {
            vm.reloadData()
        }

        activity_recycler_view.setupForVerticalLayout()
        activity_recycler_view.adapter = adapter
    }

    // DELEGATE

    override fun <M : Comparable<M>, VM : ViewModel<M>, Q : Query> didUpdateData(
            arrayViewModel: ArrayViewModel<M, VM, Q>, update: Update) {
        handler.handle(update)
    }

    override fun didChangeState(state: State) {
        super.didChangeState(state)

        when (vm.state) {
            State.error -> {
                refresh_control.isRefreshing = false
                val message = ErrorHandler.message(vm.state.errorValue!!)
                loading_view.state = ViewState.error
                loading_view.state.errorMessage = message
            }
            State.loading, State.initial -> {
                loading_view.state = ViewState.loading
            }
            State.ready -> {
                refresh_control.isRefreshing = false
                if (vm.numberOfItems == 0) {
                    loading_view.state = ViewState.error
                    loading_view.state.errorMessage = "No updates"
                }
                else {
                    loading_view.state = ViewState.ready
                }
            }
            else -> return
        }
    }

    // ADAPTER

    class Adapter(private val fr:ActivityFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            return if (position < fr.activeVoting.size) 0 else 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val i = LayoutInflater.from(parent.context)
            return if (viewType == 0) {
                VoteNotificationCell(i.inflate(R.layout.cell_activity_vote, parent, false))
            }
            else {
                ActivityViewHolder(i.inflate(R.layout.cell_activity, parent, false))
            }
        }

        override fun getItemCount(): Int {
            return fr.activeVoting.size + fr.vm.numberOfItems
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as? VoteNotificationCell)?.let {
                it.setup(fr.activeVoting[position])
                        .tap {
                            // open voting
                        }
            }

            (holder as? ActivityViewHolder)?.let {
                val pos = position - fr.activeVoting.size
                val item = fr.vm.item(pos, true)
                it.setup(item)
                        .showDecor(pos == 0, pos == fr.vm.numberOfItems - 1)
                        .activityTap {
                            // open update or something else
                        }
                        .campaignTap {
                            Service.presenter.presentCampaignFragment(fr, item.campaignId)
                        }
            }
        }
    }
}