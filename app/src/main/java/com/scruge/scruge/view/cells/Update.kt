package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.viewmodel.update.UpdateVM
import kotlinx.android.synthetic.main.cell_update.view.*
import kotlinx.android.synthetic.main.cell_update_last.view.*

class UpdateCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm:UpdateVM):UpdateCell {
        itemView.cell_update_date.text = vm.date
        itemView.cell_update_text.text = vm.description
        itemView.cell_update_title.text = vm.title
        // image?
        return this
    }
}

class LastUpdateCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    lateinit var vm:UpdateVM

    fun setup(vm:UpdateVM, title:String = R.string.title_last_update.string()):LastUpdateCell {
        this.vm = vm

        itemView.last_update_large_title.text = "$title: ${vm.date}"
        itemView.last_update_text.text = vm.description
        itemView.last_update_title.text = vm.title
        // image?

        return this
    }

    fun updateTap(tap: (UpdateVM)->Unit): LastUpdateCell {
        itemView.last_update_tap.setOnClickListener {
            tap(vm)
        }
        return this
    }

    fun allUpdatesTap(tap: ()->Unit): LastUpdateCell {
        itemView.last_update_see_all.setOnClickListener {
            tap()
        }
        return this
    }
}
