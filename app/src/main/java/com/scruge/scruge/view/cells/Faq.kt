package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.viewmodel.faq.FaqVM

class FaqCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(faqVM:FaqVM):FaqCell {

        return this
    }

    fun tap(tap: (FaqVM)->Unit): FaqCell {

        return this
    }
}