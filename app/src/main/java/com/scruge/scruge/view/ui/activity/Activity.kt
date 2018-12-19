package com.scruge.scruge.view.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.model.entity.Voting
import com.scruge.scruge.view.cells.ActivityViewHolder
import com.scruge.scruge.view.cells.VotingViewHolder
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.update.UpdateAVM
import com.ysoftware.mvvm.array.ArrayViewModelUpdateHandler
import kotlinx.android.synthetic.main.fragment_activity.*

class ActivityFragment: NavigationFragment() {

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

        setupTable()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()
        (activity as? TabbarActivity)?.tabbarHidden = false
    }

    private fun setupTable() {
        activity_recycler_view.setupForVerticalLayout()
        activity_recycler_view.adapter = adapter
    }

    // ADAPTER

    class Adapter(private val fr:ActivityFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int {
            return if (position < fr.activeVoting.size) 0 else 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val i = LayoutInflater.from(parent.context)
            return if (viewType == 0) {
                VotingViewHolder(i.inflate(R.layout.cell_activity_vote, parent, false))
            }
            else {
                ActivityViewHolder(i.inflate(R.layout.cell_activity, parent, false))
            }
        }

        override fun getItemCount(): Int {
            return fr.activeVoting.size + fr.vm.numberOfItems
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        }
    }
}