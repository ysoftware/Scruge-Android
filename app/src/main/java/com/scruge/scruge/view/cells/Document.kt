package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.viewmodel.document.DocumentAVM
import com.scruge.scruge.viewmodel.document.DocumentVM

class DocumentCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

class DocumentsCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm:DocumentAVM):DocumentsCell {

        return this
    }

    fun tap(tap: (DocumentVM)->Unit): DocumentsCell {

        return this
    }
}