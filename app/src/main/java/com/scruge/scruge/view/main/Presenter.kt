package com.scruge.scruge.view.main

import android.content.Context
import android.content.Intent
import com.scruge.scruge.dependencies.navigation.NavigationController
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.view.ui.activity.ActivityFragment
import com.scruge.scruge.view.ui.authprofile.LoginFragment
import com.scruge.scruge.view.ui.authprofile.ProfileFragment
import com.scruge.scruge.view.ui.authprofile.RegisterFragment
import com.scruge.scruge.view.ui.campaign.CampaignFragment
import com.scruge.scruge.view.ui.campaign.FeaturedFragment
import com.scruge.scruge.view.ui.wallet.WalletFragment
import com.scruge.scruge.viewmodel.campaign.CampaignVM

class Presenter {

    fun setupMainTabs(navigationControllers:List<NavigationController>) {
        navigationControllers[0].replaceWith(FeaturedFragment())
        navigationControllers[1].replaceWith(ActivityFragment())
        navigationControllers[2].replaceWith(WalletFragment())
        navigationControllers[3].replaceWith(ProfileFragment())
    }

    // AUTH

    // holds onto completion block to pass into auth activity
    private var authCompletionBlock:((Boolean)->Unit)? = null

    fun presentLoginActivity(context: Context, completion:(Boolean)->Unit) {
        val intent = Intent(context, AuthActivity::class.java)
        authCompletionBlock = completion
        context.startActivity(intent)
    }

    fun setupAuth(navigationController: NavigationController):(Boolean)->Unit {
        navigationController.replaceWith(LoginFragment())
        val block = authCompletionBlock!!
        authCompletionBlock = null
        return block
    }

    fun replaceWithLoginFragment(navigationController: NavigationController) {
        navigationController.replaceWith(LoginFragment())
    }

    fun replaceWithRegisterFragment(navigationController: NavigationController) {
        navigationController.replaceWith(RegisterFragment())
    }

    // CAMPAIGN

    fun presentCampaignFragment(vc: NavigationFragment, id:Int) {
        val fr = CampaignFragment()
        fr.vm = CampaignVM(id)
        vc.navigationController?.navigateTo(fr)
    }
}