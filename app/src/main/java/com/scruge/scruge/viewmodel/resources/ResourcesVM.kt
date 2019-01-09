package com.scruge.scruge.viewmodel.resources

import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.model.entity.Resources
import com.scruge.scruge.services.Service
import com.ysoftware.mvvm.single.ViewModel

class ResourcesVM : ViewModel<Resources>(null) {

    var accountName:String? = null

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

    // strings

    private val data get() = model?.data

    val cpuWeight get() = data?.total_resources?.cpu_weight ?: ""

    val netWeight get() = data?.total_resources?.net_weight ?: ""

    val cpu get() = "${cpuUsedValue.formatRounding(separator=".")}ms / ${cpuLimitValue.formatRounding(separator=".")}ms"

    val net get() = "${netUsedValue.formatRounding(separator=".")}KB / ${netLimitValue.formatRounding(separator=".")}KB"

    val ram get() = "${ramUsedValue}KB / ${ramLimitValue}KB"

    // values

    val cpuLimitValue get() = (data?.cpu_limit?.max?.toDouble() ?: 0.0) / 1000

    val cpuUsedValue get() = (data?.cpu_limit?.used?.toDouble() ?: 0.0) / 1000

    val netLimitValue get() = (data?.net_limit?.max?.toDouble() ?: 0.0) / 1000

    val netUsedValue get() = (data?.net_limit?.used?.toDouble() ?: 0.0) / 1000

    val ramLimitValue get() = (data?.ram_quota?.toDouble() ?: 0.0) / 1000

    val ramUsedValue get() = (data?.ram_usage?.toDouble() ?: 0.0) / 1000
}