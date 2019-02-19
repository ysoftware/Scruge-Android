package com.scruge.scruge.view.ui.bounty

import com.scruge.scruge.model.entity.Document
import com.scruge.scruge.model.entity.Range
import com.scruge.scruge.model.entity.Social


data class Exchange(val name: String, val url: String) : Comparable<Exchange> {
    override fun compareTo(other: Exchange): Int = compareValuesBy(this, other) { it.name }
}

data class ProjectEconomics(val tokenSupply: Long,
                            val annualInflationPercent: Range,
                            val listingDate: String?, val exchange: Exchange?) : Comparable<ProjectEconomics> {

    override fun compareTo(other: ProjectEconomics): Int = compareValuesBy(this, other) { it.exchange }
}

data class Project(val providerName: String, val projectName: String,
                   val projectDescription: String, val videoUrl: String,
                   val imageUrl: String,
                   val social: List<Social>,
                   val documents: List<Document>,
                   val economics: ProjectEconomics) : Comparable<Project> {
    override fun compareTo(other: Project): Int = compareValuesBy(this, other) { it.providerName }
}
