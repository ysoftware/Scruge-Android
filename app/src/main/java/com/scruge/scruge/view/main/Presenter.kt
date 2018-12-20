package com.scruge.scruge.view.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.scruge.scruge.dependencies.navigation.NavigationController
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.view.ui.activity.ActivityFragment
import com.scruge.scruge.view.ui.authprofile.EditProfileFragment
import com.scruge.scruge.view.ui.authprofile.LoginFragment
import com.scruge.scruge.view.ui.authprofile.ProfileFragment
import com.scruge.scruge.view.ui.authprofile.RegisterFragment
import com.scruge.scruge.view.ui.campaign.CampaignFragment
import com.scruge.scruge.view.ui.campaign.FeaturedFragment
import com.scruge.scruge.view.ui.details.CommentsFragment
import com.scruge.scruge.view.ui.details.ContributeFragment
import com.scruge.scruge.view.ui.details.VoteFragment
import com.scruge.scruge.view.ui.details.VoteResultFragment
import com.scruge.scruge.view.ui.wallet.*
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import com.scruge.scruge.viewmodel.comment.CommentAVM
import com.scruge.scruge.viewmodel.comment.CommentSource
import com.scruge.scruge.viewmodel.profile.ProfileVM
import com.scruge.scruge.viewmodel.update.UpdateVM
import com.theartofdev.edmodo.cropper.CropImage

class Presenter {

    fun setupMainTabs(navigationControllers:List<NavigationController>) {
        navigationControllers[0].replaceRoot(FeaturedFragment())
        navigationControllers[1].replaceRoot(ActivityFragment())
        navigationControllers[2].replaceRoot(WalletFragment())
        navigationControllers[3].replaceRoot(ProfileFragment())
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
        navigationController.replaceRoot(LoginFragment())
        val block = authCompletionBlock!!
        authCompletionBlock = null
        return block
    }

    fun replaceWithLoginFragment(fragment:NavigationFragment) {
        fragment.navigationController?.replaceRoot(LoginFragment())
    }

    fun replaceWithRegisterFragment(fragment:NavigationFragment) {
        fragment.navigationController?.replaceRoot(RegisterFragment())
    }

    fun presentProfileSetupFragment(fragment:NavigationFragment) {
        fragment.navigationController?.navigateTo(EditProfileFragment())
    }

    fun presentProfileEditFragment(fragment:NavigationFragment, vm: ProfileVM) {
        val fr = EditProfileFragment()
        fr.editingProfile = vm
        fragment.navigationController?.navigateTo(fr)
    }

    // CAMPAIGN

    fun presentCampaignFragment(fragment: NavigationFragment, id:Int) {
        val new = CampaignFragment()
        new.vm = CampaignVM(id)
        fragment.navigationController?.navigateTo(new)
    }

    // COMMENTS

    fun presentCommentsViewController(fragment:NavigationFragment, vm:CampaignVM) {
        val new = CommentsFragment()
        val source = CommentSource.campaign
        source.campaignObject = vm.model
        new.vm = CommentAVM(source)
        fragment.navigationController?.navigateTo(new)
    }

    fun presentCommentsViewController(fragment:NavigationFragment, vm: UpdateVM) {
        val new = CommentsFragment()
        val source = CommentSource.update
        source.updateObject = vm.model
        new.vm = CommentAVM(source)
        fragment.navigationController?.navigateTo(new)
    }

    // PROFILE

    fun presentImagePickerActivity(fragment: NavigationFragment) {
        fragment.activity?.let { CropImage.startPickImageActivity(it, fragment) }
    }

    fun presentCropImageActivity(fragment: NavigationFragment, imageUrl: Uri) {
        fragment.activity?.let { CropImage.activity(imageUrl).start(it, fragment) }
    }

    // CONTRIBUTION

    fun presentContributeFragment(fragment: NavigationFragment, vm:CampaignVM) {
        val fr = ContributeFragment()
        fr.vm = vm
        fragment.navigationController?.navigateTo(fr)
    }

    fun presentVoteFragment(fragment: NavigationFragment, vm:CampaignVM) {
        val fr = VoteFragment()
        fr.vm = vm
        fragment.navigationController?.navigateTo(fr)
    }

    fun presentVoteResultsFragment(fragment: NavigationFragment, vm:CampaignVM) {
        val fr = VoteResultFragment()
        fr.vm = vm
        fragment.navigationController?.navigateTo(fr)
    }

    // WALLET

    fun presentImporKeyFragment(fragment: NavigationFragment) {
        fragment.navigationController?.navigateTo(ImportKeyFragment())
    }

    fun presentCreateAccountFragment(fragment: NavigationFragment) {
        fragment.navigationController?.navigateTo(CreateAccountFragment())
    }

    fun replaceWithImportKeyFragment(fragment: NavigationFragment) {
        fragment.navigationController?.replaceTop(ImportKeyFragment())
    }

    fun replaceWithCreateAccountFragment(fragment: NavigationFragment) {
        fragment.navigationController?.replaceTop(CreateAccountFragment())
    }

    fun presentWalletPicker(fragment: NavigationFragment) {
        fragment.startActivityForResult(Intent(fragment.context, WalletPickerActivity::class.java), 0)
    }

    fun replaceWithWalletFragment(fragment: NavigationFragment) {
        fragment.navigationController?.replaceRoot(WalletFragment())
    }

    fun replaceWithWalletStartFragment(fragment: NavigationFragment) {
        fragment.navigationController?.replaceRoot(WalletStartFragment())
    }

    fun replaceWithWalletNoAccountFragment(fragment: NavigationFragment) {
        fragment.navigationController?.replaceRoot(WalletNoAccountFragment())
    }
}