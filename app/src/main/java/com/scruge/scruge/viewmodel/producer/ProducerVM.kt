package com.scruge.scruge.viewmodel.producer

import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.model.entity.Producer
import com.ysoftware.mvvm.single.ViewModel

class ProducerVM(model: Producer?, val totalVotesWeight:String) : ViewModel<Producer>(model) {

    val name get() = model?.producer?.owner ?: ""

    val votes:String get() {
        val weights = model?.producer?.total_votes?.toDouble() ?: return ""
        val total = totalVotesWeight.toDoubleOrNull() ?: return ""
        val percent = (weights / (total / 100)).formatRounding()
        return percent + "%"
    }
}