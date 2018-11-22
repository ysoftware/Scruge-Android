package com.scruge.scruge.viewmodel.campaign

import java.net.URL

interface PartialCampaignViewModel {

    val description: String

    val title: String

    val progress: Double

    val progressString: String

    val raisedString: String

    val daysLeft:String

    val imageUrl: URL?
}

interface PartialCampaignModelHolder<Model> {

    var model:Model?
}