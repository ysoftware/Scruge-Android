package com.scruge.scruge.view.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.ViewState
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.cells.UpdateCell
import com.scruge.scruge.viewmodel.update.UpdateAVM
import com.ysoftware.mvvm.array.*
import com.ysoftware.mvvm.single.ViewModel
import kotlinx.android.synthetic.main.fragment_updates.*

class UpdatesFragment : NavigationFragment(), ArrayViewModelDelegate {

    lateinit var vm: UpdateAVM
    private val adapter = Adapter(this)
    private val handler = ArrayViewModelUpdateHandler(adapter)

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_updates, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
        setupTable()
        setupActions()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()
        setupNavigationBar()
    }

    private fun setupVM() {
        vm.delegate = this
        vm.reloadData()
    }

    private fun setupNavigationBar() {
        shouldHideNavigationBar = false
        title = R.string.title_updates.string()
    }

    private fun setupTable() {
        updates_recycler_view.setupForVerticalLayout()
        updates_recycler_view.adapter = adapter
    }

    private fun setupActions() {
        updates_refresh_control.setOnRefreshListener { vm.reloadData() }
    }

    override fun didChangeState(state: State) {
        when (state) {
            State.initial -> updates_loading_view.state = ViewState.loading
            State.error -> {
                updates_loading_view.state = ViewState.error
                updates_loading_view.state.errorMessage = ErrorHandler.message(state.errorValue)
                updates_refresh_control.isRefreshing = false
            }
            State.ready -> {
                updates_refresh_control.isRefreshing = false
                if (vm.isEmpty()) {
                    updates_loading_view.state = ViewState.error
                    updates_loading_view.state.errorMessage = R.string.error_no_comments.string()
                }
                else {
                    updates_loading_view.state = ViewState.ready
                }
            }
            else -> {}
        }
    }

    override fun <M : Comparable<M>, VM : ViewModel<M>, Q : Query> didUpdateData(
            arrayViewModel: ArrayViewModel<M, VM, Q>, update: Update) {
        handler.handle(update)
    }

    class Adapter(val fr:UpdatesFragment) : RecyclerView.Adapter<UpdateCell>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpdateCell {
            return UpdateCell(LayoutInflater.from(parent.context)
                                       .inflate(R.layout.cell_update, parent, false))
        }

        override fun getItemCount(): Int = fr.vm.numberOfItems

        override fun onBindViewHolder(holder: UpdateCell, position: Int) {
            val item = fr.vm.item(position, true)
            holder.setup(item)
            holder.itemView.setOnClickListener {
                Service.presenter.presentContentFragment(fr, item)
            }
        }
    }
}