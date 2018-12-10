package com.scruge.scruge.view.ui.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.view.cells.CampaignCell
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import com.ysoftware.mvvm.single.ViewModel
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.campaign_fragment.*

class CampaignFragment: NavigationFragment(), ViewModelDelegate {

    enum class Block(val rawValue:Int) {
        info(0),  economies(1), update(2), comments(3),
        about(4), faq(5), milestone(6), documents(7)
    }

    // PROPERTIES

    var vm: CampaignVM? = null
    var adapter:Adapter? = null

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
        vm?.let { vm ->
            vm.delegate = this
            vm.load()
            adapter = Adapter(vm)
        }
    }

    private fun setupTable() {
        recycler_view.setupForVerticalLayout()
        recycler_view.adapter = adapter
    }

    // DELEGATE

    override fun <M : Comparable<M>> didUpdateData(viewModel: ViewModel<M>) {

    }

    // ADAPTER

    class Adapter(private val vm: CampaignVM): RecyclerView.Adapter<CampaignCell>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignCell {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.cell_campaign_small, parent, false)
            return CampaignCell(view)
        }

        override fun getItemCount(): Int {
            return 0
        }

        override fun onBindViewHolder(holder: CampaignCell, position: Int) {

        }
    }

}