package com.scruge.scruge.services.api.model

import com.scruge.scruge.dependencies.serialization.Codable
import com.scruge.scruge.model.entity.ActivityModel
import com.scruge.scruge.model.entity.Milestone
import com.scruge.scruge.model.entity.Update

data class MilestoneListResponse(val milestones:List<Milestone>)

data class UpdateListResponse(val updates:List<Update>)

data class ActivityListResponse(val activity: List<ActivityModel>)

data class ActivityListRequest(val page:Int): Codable