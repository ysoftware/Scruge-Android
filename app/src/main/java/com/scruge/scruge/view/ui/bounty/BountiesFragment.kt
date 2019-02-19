package com.scruge.scruge.view.ui.bounty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.cells.BountyCell
import com.ysoftware.mvvm.array.ArrayViewModel
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.array.Query
import com.ysoftware.mvvm.array.Update
import com.ysoftware.mvvm.single.ViewModel
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_bounty_bounties.*

class BountiesFragment : NavigationFragment(), ViewModelDelegate, ArrayViewModelDelegate {

    private lateinit var bountyAVM: BountyAVM
    private lateinit var adapter:Adapter
    lateinit var projectVM: ProjectVM

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bounty_bounties, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
        setupTable()
    }

    private fun setupVM() {
        bountyAVM = BountyAVM(projectVM)
        bountyAVM.delegate = this
        adapter = Adapter(bountyAVM)
        bountyAVM.reloadData()
    }

    private fun setupTable() {
        title = projectVM.name
        recycler_view.setupForVerticalLayout()
        recycler_view.adapter = adapter

        adapter.tap = { index ->
            Service.presenter.presentBountyFragment(this, bountyAVM.item(index), projectVM)
        }
    }

    override fun <M : Comparable<M>, VM : ViewModel<M>, Q : Query> didUpdateData(
            arrayViewModel: ArrayViewModel<M, VM, Q>, update: Update) {
        activity?.runOnUiThread {
            recycler_view.adapter?.notifyDataSetChanged()
        }
    }

    // ADAPTER

    class Adapter(private val vm: BountyAVM) : RecyclerView.Adapter<BountyCell>() {

        var tap: ((Int) -> Unit)? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BountyCell {
            return BountyCell(
                    LayoutInflater.from(parent.context).inflate(R.layout.cell_bounty, parent, false))
        }

        override fun getItemCount(): Int {
            return vm.numberOfItems
        }

        override fun onBindViewHolder(holder: BountyCell, position: Int) {
            holder.setup(vm.item(position, true))
            holder.itemView.setOnClickListener {
                tap?.invoke(position)
            }
        }
    }
}