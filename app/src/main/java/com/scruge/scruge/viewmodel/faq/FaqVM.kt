package com.scruge.scruge.viewmodel.faq

import com.scruge.scruge.model.entity.Faq
import com.ysoftware.mvvm.single.ViewModel

class FaqVM(model: Faq?) : ViewModel<Faq>(model) {

    val question get() = model?.question ?: ""

    val answer get() = model?.answer ?: ""
}