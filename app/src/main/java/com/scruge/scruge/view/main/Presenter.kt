package com.scruge.scruge.view.main

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.fragment.app.Fragment
import com.scruge.scruge.dependencies.navigation.NavigationController
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.view.ui.activity.ActivityFragment
import com.scruge.scruge.view.ui.authprofile.EditProfileFragment
import com.scruge.scruge.view.ui.authprofile.LoginFragment
import com.scruge.scruge.view.ui.authprofile.ProfileFragment
import com.scruge.scruge.view.ui.authprofile.RegisterFragment
import com.scruge.scruge.view.ui.campaign.CampaignFragment
import com.scruge.scruge.view.ui.campaign.FeaturedFragment
import com.scruge.scruge.view.ui.wallet.*
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import com.scruge.scruge.viewmodel.profile.ProfileVM
import com.theartofdev.edmodo.cropper.CropImage

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

    fun replaceWithLoginFragment(fragment:NavigationFragment) {
        fragment.navigationController?.replaceWith(LoginFragment())
    }

    fun replaceWithRegisterFragment(fragment:NavigationFragment) {
        fragment.navigationController?.replaceWith(RegisterFragment())
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

    // PROFILE

    fun presentImagePickerActivity(fragment: NavigationFragment) {
        fragment.activity?.let { CropImage.startPickImageActivity(it, fragment) }
    }

    fun presentCropImageActivity(fragment: NavigationFragment, imageUrl: Uri) {
        fragment.activity?.let { CropImage.activity(imageUrl).start(it, fragment) }
    }

    // WALLET

    fun presentImporKeyFragment(fragment: NavigationFragment) {
        fragment.navigationController?.navigateTo(ImportKeyFragment())
    }

    fun presentCreateAccountFragment(fragment: NavigationFragment) {
        fragment.navigationController?.navigateTo(CreateAccountFragment())
    }

    fun replaceWithImportKeyFragment(fragment: NavigationFragment) {
        fragment.navigationController?.replaceWith(ImportKeyFragment())
    }

    fun replaceWithCreateAccountFragment(fragment: NavigationFragment) {
        fragment.navigationController?.replaceWith(CreateAccountFragment())
    }

    fun presentWalletPicker(fragment: NavigationFragment) {
//        fragment.startActivity(Intent(fragment.context, ))
    }

    fun replaceWithWalletFragment(fragment: NavigationFragment) {
        fragment.navigationController?.replaceWith(WalletFragment())
    }

    fun replaceWithWalletStartFragment(fragment: NavigationFragment) {
        fragment.navigationController?.replaceWith(WalletStartFragment())
    }

    fun replaceWithWalletNoAccountFragment(fragment: NavigationFragment) {
        fragment.navigationController?.replaceWith(WalletNoAccountFragment())
    }
}