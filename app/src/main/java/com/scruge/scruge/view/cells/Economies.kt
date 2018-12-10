package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.viewmodel.economies.EconomiesVM
import kotlinx.android.synthetic.main.cell_economies.view.*

class EconomiesCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm:EconomiesVM):EconomiesCell {

        itemView.economies_inflation.text = vm.inflationRate
        itemView.economies_initial_release.text = vm.initialRelease
        itemView.economies_public_percent.text = vm.publicPercent
        itemView.economies_supply.text = vm.tokenSupply
        return this
    }
}