package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.dependencies.view.setImage
import com.scruge.scruge.view.ui.bounty.BountyVM
import com.scruge.scruge.view.ui.bounty.ProjectVM
import kotlinx.android.synthetic.main.cell_bounty.view.*
import kotlinx.android.synthetic.main.cell_project.view.*

class BountyCell(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun setup(vm: BountyVM) {
        itemView.cell_bounty_details.text = vm.shortDescription
        itemView.cell_bounty_dates.text = vm.dates
        itemView.cell_bounty_title.text = vm.name
    }
}

class ProjectCell(itemView: View): RecyclerView.ViewHolder(itemView) {

    fun setup(vm: ProjectVM) {
        itemView.cell_project_description.text = vm.description
        itemView.cell_project_image.setImage(vm.imageUrl, hideOnFail = true)
        itemView.cell_project_title.text = vm.name
    }
}