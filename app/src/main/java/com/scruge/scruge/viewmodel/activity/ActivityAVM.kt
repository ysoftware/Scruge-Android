package com.scruge.scruge.viewmodel.activity

import com.scruge.scruge.model.entity.ActivityModel
import com.scruge.scruge.model.error.GeneralError
import com.scruge.scruge.model.error.wrap
import com.scruge.scruge.services.Service
import com.ysoftware.mvvm.array.ArrayViewModel

class ActivityAVM: ArrayViewModel<ActivityModel, ActivityVM, ActivityQ>() {

    override fun fetchData(query: ActivityQ?, block: (Result<Collection<ActivityVM>>) -> Unit) {
        Service.api.getActivity(query) { result ->
            block(result.map { it.activity.map { ActivityVM(it) } })
        }
    }
}