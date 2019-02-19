package com.scruge.scruge.view.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationController
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.Member
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.ui.activity.ActivityFragment
import com.scruge.scruge.view.ui.authprofile.EditProfileFragment
import com.scruge.scruge.view.ui.authprofile.LoginFragment
import com.scruge.scruge.view.ui.authprofile.ProfileFragment
import com.scruge.scruge.view.ui.authprofile.RegisterFragment
import com.scruge.scruge.view.ui.bounty.BountyVM
import com.scruge.scruge.view.ui.bounty.EarnFragment
import com.scruge.scruge.view.ui.bounty.ProjectVM
import com.scruge.scruge.view.ui.campaign.CampaignFragment
import com.scruge.scruge.view.ui.campaign.FeaturedFragment
import com.scruge.scruge.view.ui.campaign.MemberProfileFragment
import com.scruge.scruge.view.ui.details.*
import com.scruge.scruge.view.ui.wallet.*
import com.scruge.scruge.viewmodel.account.AccountVM
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import com.scruge.scruge.viewmodel.comment.CommentAVM
import com.scruge.scruge.viewmodel.comment.CommentSource
import com.scruge.scruge.viewmodel.comment.CommentVM
import com.scruge.scruge.viewmodel.faq.FaqVM
import com.scruge.scruge.viewmodel.milestone.MilestoneVM
import com.scruge.scruge.viewmodel.profile.ProfileVM
import com.scruge.scruge.viewmodel.update.UpdateAVM
import com.scruge.scruge.viewmodel.update.UpdateVM
import com.theartofdev.edmodo.cropper.CropImage

class Presenter {

    fun setupMainTabs(navigationControllers:List<NavigationController>) {
        navigationControllers[0].replaceRoot(FeaturedFragment())
        navigationControllers[1].replaceRoot(ActivityFragment())
        navigationControllers[2].replaceRoot(EarnFragment())
        navigationControllers[3].replaceRoot(WalletFragment())
        navigationControllers[4].replaceRoot(ProfileFragment())
    }

    // BOUNTIES

    fun presentProjectFragment(fragment:NavigationFragment, projectVM: ProjectVM) {

    }

    fun presentBountiesFragment(fragment: NavigationFragment, projectVM: ProjectVM) {

    }

    fun presentBountyFragment(fragment: NavigationFragment, bountyVM: BountyVM, projectVM: ProjectVM) {

    }

    fun presentSubmitFragment(fragment: NavigationFragment, bountyVM: BountyVM, projectVM: ProjectVM) {

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

    fun presentMemberProfileFragment(fragment: NavigationFragment, member: Member) {
        val fr = MemberProfileFragment()
        fr.member = member
        fragment.navigationController?.navigateTo(fr)
    }

    // CAMPAIGN

    fun presentCampaignFragment(fragment: NavigationFragment, id:Int) {
        val new = CampaignFragment()
        new.vm = CampaignVM(id)
        fragment.navigationController?.navigateTo(new)
    }

    fun presentFaqFragment(fragment: NavigationFragment, vm: FaqVM) {
        val new = DetailFragment()
        new.faq = vm
        fragment.navigationController?.navigateTo(new)
    }

    fun presentMilestoneFragment(fragment: NavigationFragment, vm: MilestoneVM) {
        val new = DetailFragment()
        new.milestone = vm
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

    fun presentCommentsViewController(fragment:NavigationFragment,
                                      avm:CommentAVM,
                                      commentVM: CommentVM,
                                      presentKeyboard:Boolean = false) {
        val model = commentVM.model ?: return
        val commentSource = avm.query?.source ?: return

        val new = CommentsFragment()
        val source = CommentSource.comment
        source.commentObject = model
        source.parentCommentSource = commentSource
        new.vm = CommentAVM(source)
        new.shouldOpenTyping = presentKeyboard
        fragment.navigationController?.navigateTo(new)
    }

    // UPDATE

    fun presentUpdatesFragment(fragment: NavigationFragment, vm:CampaignVM) {
        val fr = UpdatesFragment()
        val source = UpdateAVM.Source.campaign
        source.campaignObject = vm.model!!
        fr.vm = UpdateAVM(source)
        fragment.navigationController?.navigateTo(fr)
    }

    fun presentContentFragment(fragment: NavigationFragment, vm:CampaignVM) {
        val new = BrowserFragment()
        new.campaignVM = vm
        fragment.navigationController?.navigateTo(new)
    }

    fun presentContentFragment(fragment: NavigationFragment, vm:UpdateVM) {
        val new = BrowserFragment()
        new.updateVM = vm
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
        if (Service.settings.didCreateEosAccount) {
            val msg = R.string.alert_previously_created_eos.string()
            fragment.alert(msg)
            return
        }
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

    fun presentStakeFragment(fragment: NavigationFragment, vm:AccountVM) {
        val fr = StakeFragment()
        fr.accountVM = vm
        fragment.navigationController?.navigateTo(fr)
    }

    fun presentBuyRAMFragment(fragment: NavigationFragment, vm:AccountVM) {
        val fr = BuyRAMFragment()
        fr.accountVM = vm
        fragment.navigationController?.navigateTo(fr)
    }

    fun presentTransferFragment(fragment: NavigationFragment, vm:AccountVM) {
        val fr = TransferFragment()
        fr.accountVM = vm
        fragment.navigationController?.navigateTo(fr)
    }

    fun presentVoteBPFragment(fragment: NavigationFragment, vm:AccountVM) {
        val fr = VoteBPFragment()
        fr.accountVM = vm
        fragment.navigationController?.navigateTo(fr)
    }

    // OTHER

    fun presentPrivacyPolicy(fragment: NavigationFragment) {
        val url = "https://scruge.world/privacy"
        presentBrowser(fragment, url, R.string.title_privacy.string())
    }

    fun presentSettingsFragment(fragment: NavigationFragment, vm:ProfileVM) {
        val fr = SettingsFragment()
        fr.profileVM = vm
        fragment.navigationController?.navigateTo(fr)
    }

    fun presentBrowser(fragment: NavigationFragment,
                       url: String,
                       title:String = R.string.title_preview.string()) {
        val new = BrowserFragment()
        new.title = title
        new.url = url
        fragment.navigationController?.navigateTo(new)
    }
}