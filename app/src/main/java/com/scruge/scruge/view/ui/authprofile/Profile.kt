package com.scruge.scruge.view.ui.authprofile

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.setImage
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.profile.ProfileVM
import com.ysoftware.mvvm.single.ViewModel
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment: NavigationFragment(), ViewModelDelegate {

    private var vm = ProfileVM()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupActions()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()

        setupVM()
        setupNavigationBar()
    }

    private fun setupVM() {
        vm.delegate = this
        vm.load()
    }

    private fun setupNavigationBar() {
        (activity as? TabbarActivity)?.tabbarHidden = false
        shouldHideNavigationBar = true
    }

    private fun setupActions() {
        profile_settings.setOnClickListener {
            Service.presenter.presentSettingsFragment(this, vm)
        }
    }

    override fun <M : Comparable<M>> didUpdateData(viewModel: ViewModel<M>) {
        activity?.runOnUiThread {
            refreshProfile()
        }
    }

    private fun refreshProfile() {
        profile_email.text = vm.email
        profile_name.text = if (vm.name.isBlank()) R.string.label_anonymous.string() else vm.name
        profile_location.text = vm.country
        profile_about.text = 
                if (vm.description.isBlank()) R.string.label_profile_no_information.string()
                else vm.description
        profile_image.setImage(vm.imageUrl)
    }
}