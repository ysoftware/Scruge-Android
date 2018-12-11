package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.viewmodel.faq.FaqVM
import kotlinx.android.synthetic.main.cell_faq.view.*

class FaqCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(faqVM:FaqVM):FaqCell {
        itemView.faq_answer.text = faqVM.answer
        itemView.faq_question.text = faqVM.question
        return this
    }
}