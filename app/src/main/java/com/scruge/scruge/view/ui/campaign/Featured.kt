package com.scruge.scruge.view.ui.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.verticalLayout
import com.scruge.scruge.view.cells.CampaignCell
import com.scruge.scruge.viewmodel.ViewState
import com.scruge.scruge.viewmodel.campaign.CampaignAVM
import com.ysoftware.mvvm.array.*
import com.ysoftware.mvvm.single.ViewModel
import kotlinx.android.synthetic.main.featured_fragment.*

class FeaturedFragment: Fragment(), ArrayViewModelDelegate {

    // PROPERTIES

    private val vm = CampaignAVM()
    private val adapter = Adapter(vm)
    private val updateHandler = ArrayViewModelUpdateHandler(adapter)

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.featured_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
        setupTable()
    }

    private fun setupVM() {
        vm.delegate = this
        vm.reloadData()
    }

    private fun setupTable() {
        refresh_control.setOnRefreshListener {
            vm.reloadData()
        }

        recycler_view.verticalLayout()
        recycler_view.adapter = adapter
    }

    // VIEW MODEL

    override fun <M : Comparable<M>, VM : ViewModel<M>, Q : Query> didUpdateData(
            arrayViewModel: ArrayViewModel<M, VM, Q>, update: Update) {
        updateHandler.handle(update)
    }

    override fun didChangeState(state: State) {
        when (state) {
            State.error -> {
                loading_view.state = ViewState.error
                loading_view.state.errorMessage = state.errorValue?.message ?: "" // todo
                refresh_control.isRefreshing = false
            }
            State.loading, State.initial -> {
                loading_view.state = ViewState.loading
            }
            State.ready -> {
                refresh_control.isRefreshing = false
                if (vm.numberOfItems == 0) {
                    loading_view.state = ViewState.error
                    loading_view.state.errorMessage = "No campaigns were found for your request"
                }
                else {
                    loading_view.state = ViewState.ready
                }
            }
            else -> return
        }
    }

    // ADAPTER

    class Adapter(private val vm:CampaignAVM): RecyclerView.Adapter<CampaignCell>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignCell {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_campaign_small, parent, false)
            return CampaignCell(view)
        }

        override fun getItemCount(): Int {
            return vm.numberOfItems
        }

        override fun onBindViewHolder(holder: CampaignCell, position: Int) {
            holder.setup(vm.item(position, true))
        }
    }
}