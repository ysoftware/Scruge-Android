package com.scruge.scruge.services.api.model

import android.app.Activity
import com.scruge.scruge.model.entity.Milestone
import com.scruge.scruge.model.entity.Update

data class MilestoneListResponse(val milestones:List<Milestone>)

data class UpdateListResponse(val updates:List<Update>)

data class ActivityListResponse(val updates: List<Activity>)