package com.scruge.scruge.viewmodel.economies

import com.scruge.scruge.dependencies.dataformatting.format
import com.scruge.scruge.model.entity.Economics
import com.ysoftware.mvvm.single.ViewModel

class EconomiesVM(model: Economics?) : ViewModel<Economics>(model) {

    val tokenSupply:String get() = model?.tokenSupply?.format() ?: ""

    val publicPercent:String get() = model?.publicTokenPercent?.format()?.let { "$it%" } ?: ""

    val initialRelease:String get() = model?.initialFundsReleasePercent?.format()?.let { "$it%" } ?: ""

    val inflationRate:String get() {
        return model?.let {
            val start = it.annualInflationPercent.start.format()
            val end = it.annualInflationPercent.end.format()
            if (it.annualInflationPercent.start != it.annualInflationPercent.end)
                "$start% - $end%"
                else "$start%"
        } ?: ""
    }
}