package com.scruge.scruge.viewmodel.economies

import com.scruge.scruge.dependencies.dataformatting.formatDecimal
import com.scruge.scruge.model.entity.Economics
import com.ysoftware.mvvm.single.ViewModel

class EconomiesVM(model: Economics?) : ViewModel<Economics>(model) {

    val tokenSupply:String get() = model?.tokenSupply?.formatDecimal() ?: ""

    val publicPercent:String get() = model?.publicTokenPercent?.formatDecimal()?.let { "$it%" } ?: ""

    val initialRelease:String get() = model?.initialFundsReleasePercent?.formatDecimal()?.let { "$it%" } ?: ""

    val inflationRate:String get() {
        return model?.let {
            val start = it.annualInflationPercent.start.formatDecimal()
            val end = it.annualInflationPercent.end.formatDecimal()
            if (it.annualInflationPercent.start != it.annualInflationPercent.end)
                "$start% - $end%"
                else "$start%"
        } ?: ""
    }
}