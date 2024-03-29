package com.scruge.scruge.view.ui.bounty

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
import com.scruge.scruge.view.cells.ProjectCell
import com.scruge.scruge.view.main.TabbarActivity
import com.ysoftware.mvvm.array.*
import com.ysoftware.mvvm.single.ViewModel
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_featured.*

class EarnFragment : NavigationFragment(), ViewModelDelegate, ArrayViewModelDelegate {

    // PROPERTIES

    private val vm = ProjectAVM()
    private val adapter = Adapter(vm)
    private val updateHandler = ArrayViewModelUpdateHandler(adapter)

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bounty_projects, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
        setupTable()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()

        setupNavigationBar()

        when (vm.state) {
            State.error, State.ready -> {
                if (vm.isEmpty()) {
                    vm.reloadData()
                }
            }
        }
    }

    private fun setupNavigationBar() {
        (activity as? TabbarActivity)?.tabbarHidden = false
        shouldHideNavigationBar = false
        title = R.string.title_earn.string()
    }

    private fun setupVM() {
        vm.delegate = this
        vm.reloadData()
    }

    private fun setupTable() {
        refresh_control.setOnRefreshListener {
            vm.reloadData()
        }

        recycler_view.setupForVerticalLayout()
        recycler_view.adapter = adapter

        adapter.tap = { index ->
            Service.presenter.presentProjectFragment(this, vm.item(index))
        }
    }

    // VIEW MODEL

    override fun <M : Comparable<M>, VM : ViewModel<M>, Q : Query> didUpdateData(
            arrayViewModel: ArrayViewModel<M, VM, Q>, update: Update) {
        activity?.runOnUiThread {
            updateHandler.handle(update)
        }
    }

    override fun didChangeState(state: State) {
        when (state) {
            State.error -> {
                loading_view.state = ViewState.error
                loading_view.state.errorMessage = ErrorHandler.message(state.errorValue)
                refresh_control.isRefreshing = false
            }
            State.loading, State.initial -> {
                loading_view.state = ViewState.loading
            }
            State.ready -> {
                refresh_control.isRefreshing = false
                if (vm.numberOfItems == 0) {
                    loading_view.state = ViewState.error
                    loading_view.state.errorMessage = R.string.error_no_projects.string()
                }
                else {
                    loading_view.state = ViewState.ready
                }
            }
            else -> return
        }
    }

    // ADAPTER

    class Adapter(private val vm: ProjectAVM) : RecyclerView.Adapter<ProjectCell>() {

        var tap: ((Int) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectCell {
            return ProjectCell(
                    LayoutInflater.from(parent.context).inflate(R.layout.cell_project, parent, false))
        }

        override fun getItemCount(): Int {
            return vm.numberOfItems
        }

        override fun onBindViewHolder(holder: ProjectCell, position: Int) {
            holder.setup(vm.item(position, true))
            holder.itemView.setOnClickListener {
                tap?.invoke(position)
            }
        }
    }
}