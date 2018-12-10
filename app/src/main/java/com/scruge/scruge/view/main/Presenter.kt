package com.scruge.scruge.view.main

import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.view.ui.activity.ActivityFragment
import com.scruge.scruge.view.ui.authprofile.ProfileFragment
import com.scruge.scruge.view.ui.campaign.CampaignFragment
import com.scruge.scruge.view.ui.campaign.FeaturedFragment
import com.scruge.scruge.view.ui.wallet.WalletFragment
import com.scruge.scruge.viewmodel.campaign.CampaignVM

class Presenter {

    fun setupMainTabs(activity: TabbarActivity) {
        val featured = FeaturedFragment()
        activity.navigationControllers[0].replaceWith(featured)

        val activities = ActivityFragment()
        activity.navigationControllers[1].replaceWith(activities)

        val wallet = WalletFragment()
        activity.navigationControllers[2].replaceWith(wallet)

        val profile = ProfileFragment()
        activity.navigationControllers[3].replaceWith(profile)
    }

    fun presentCampaignFragment(vc: NavigationFragment, id:Int) {
        val fr = CampaignFragment()
        fr.vm = CampaignVM(id)
        vc.navigationController?.navigateTo(fr)
    }
}