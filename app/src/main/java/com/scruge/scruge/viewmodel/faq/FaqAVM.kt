package com.scruge.scruge.viewmodel.faq

import com.scruge.scruge.model.entity.Faq
import com.ysoftware.mvvm.array.SimpleArrayViewModel

class FaqAVM(list:List<Faq>): SimpleArrayViewModel<Faq, FaqVM>() {

    init {
        setData(list.map { FaqVM(it) })
    }

    override fun fetchData(block: (Result<Collection<FaqVM>>) -> Unit) {
        // no-op
    }
}