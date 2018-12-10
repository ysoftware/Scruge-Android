package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.viewmodel.update.UpdateVM

class UpdateCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

class LastUpdateCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm:UpdateVM):LastUpdateCell {

        return this
    }

    fun updateTap(tap: (UpdateVM)->Unit): LastUpdateCell {

        return this
    }

    fun allUpdatesTap(tap: ()->Unit): LastUpdateCell {

        return this
    }
}
