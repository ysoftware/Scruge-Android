package com.scruge.scruge.viewmodel.resources

import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.Resources
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.eos.EosName
import com.scruge.scruge.services.eos.toEosName
import com.ysoftware.mvvm.single.ViewModel

class ResourcesVM : ViewModel<Resources>(null) {

    var accountName: EosName? = null

    fun load() {
        val name = accountName
        if (name == null) {
            model = null
            return notifyUpdated()
        }

        Service.eos.getResources(name) { result ->
            result.onSuccess {
                model = result.getOrNull()
                notifyUpdated()
            }.onFailure {
                model = null
                notifyUpdated()
            }
        }
    }

    private val data get() = model?.data

    val displayName:String get() = accountName?.toString() ?: ""

    // strings

    val cpuWeight get() = data?.total_resources?.cpu_weight ?: ""

    val netWeight get() = data?.total_resources?.net_weight ?: ""

    val cpu get() = R.string.label_ms_current.string(cpuUsedValue.formatRounding(separator="."),
                                                     cpuLimitValue.formatRounding(separator="."))

    val net get() =  R.string.label_kb_current.string(netUsedValue.formatRounding(separator="."),
                                                      netLimitValue.formatRounding(separator="."))

    val ram get() = R.string.label_kb_current.string(ramUsedValue.formatRounding(separator="."),
                                                     ramLimitValue.formatRounding(separator="."))

    // values

    val cpuLimitValue get() = (data?.cpu_limit?.max?.toDouble() ?: 0.0) / 1000

    val cpuUsedValue get() = (data?.cpu_limit?.used?.toDouble() ?: 0.0) / 1000

    val netLimitValue get() = (data?.net_limit?.max?.toDouble() ?: 0.0) / 1000

    val netUsedValue get() = (data?.net_limit?.used?.toDouble() ?: 0.0) / 1000

    val ramLimitValue get() = (data?.ram_quota?.toDouble() ?: 0.0) / 1000

    val ramUsedValue get() = (data?.ram_usage?.toDouble() ?: 0.0) / 1000
}