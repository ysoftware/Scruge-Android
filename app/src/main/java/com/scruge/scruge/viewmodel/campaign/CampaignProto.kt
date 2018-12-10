package com.scruge.scruge.viewmodel.campaign

import android.net.Uri

interface PartialCampaignViewModel {

    val description: String

    val title: String

    val raised: Double

    val hardCap: Int

    val softCap: Int 

    val daysLeft:String

    val imageUrl: Uri?
}

interface PartialCampaignModelHolder<Model> {

    var model:Model?
}