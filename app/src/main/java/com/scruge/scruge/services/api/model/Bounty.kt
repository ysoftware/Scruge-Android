package com.scruge.scruge.services.api.model

import com.scruge.scruge.dependencies.serialization.Codable
import com.scruge.scruge.view.ui.bounty.Bounty
import com.scruge.scruge.view.ui.bounty.Project

// REQUEST

data class ProjectRequest(val page: Int) : Codable

data class BountiesRequest(val providerName: String) : Codable

data class SubmissionRequest(val token: String, val bountyId: Long, val proof: String, val hunterName: String,
                             val providerName: String) : Codable

// RESPONSE

data class ProjectsResponse(val projects: List<Project>)

data class BountiesResponse(val bounties: List<Bounty>)