package com.scruge.scruge.view.cells

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.dependencies.view.setImage
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import com.scruge.scruge.viewmodel.campaign.PartialCampaignVM
import com.scruge.scruge.viewmodel.campaign.PartialCampaignViewModel
import kotlinx.android.synthetic.main.cell_campaign_info.view.*
import kotlinx.android.synthetic.main.cell_campaign_small.view.*

class CampaignSmallCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm: PartialCampaignViewModel) {
        itemView.campaign_small_title.text = vm.title
        itemView.campaign_small_date.text = vm.daysLeft
        itemView.campaign_small_description.text = vm.description
        itemView.campaign_small_image.setImage(vm.imageUrl)

        itemView.campaign_small_progress_view.value = vm.raised
        itemView.campaign_small_progress_view.total = vm.hardCap.toDouble()
        itemView.campaign_small_progress_view.firstGoal = vm.softCap.toDouble()
    }
}

class CampaignInfoCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var imageUrl: Uri? = null
    private var didLoadMedia = false

    fun setup(vm: CampaignVM) {
        imageUrl = vm.imageUrl

        itemView.campaign_info_title.text = vm.title
        itemView.campaign_info_description.text = vm.description
        itemView.campaign_info_date.text = vm.daysLeft

        itemView.campaign_progress_view.value = vm.raised
        itemView.campaign_progress_view.total = vm.hardCap.toDouble()
        itemView.campaign_progress_view.firstGoal = vm.softCap.toDouble()

        val url = vm.videoUrl
        if (url != null) {
            setupWebView(url.toString())
        }
        else {
            setupImageView()
        }
    }

    private fun setupImageView() {
        itemView.campaign_info_web_view.visibility = View.GONE
        itemView.campaign_info_image.visibility = View.VISIBLE
        itemView.campaign_info_image.setImage(imageUrl)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(url:String) {
        if (didLoadMedia) return
        val wv = itemView.campaign_info_web_view

        // todo setup
        itemView.campaign_info_web_view.visibility = View.VISIBLE
        itemView.campaign_info_image.visibility = View.GONE
        wv.settings.javaScriptEnabled = true

        wv.loadUrl(url)
        didLoadMedia = true
    }
}
