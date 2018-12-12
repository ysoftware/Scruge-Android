package com.scruge.scruge.view.ui.authprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.setImage
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

        setupVM()
        setupActions()
    }

    override fun onResume() {
        super.onResume()
        vm.load()
    }

    private fun setupVM() {
        vm.delegate = this
    }

    private fun setupActions() {
        profile_signout.setOnClickListener {
            Service.tokenManager.removeToken()
            (activity as? TabbarActivity)?.selectTab(0)
        }

        profile_edit.setOnClickListener {
            Service.presenter.presentProfileEditFragment(this, vm)
        }
    }

    override fun <M : Comparable<M>> didUpdateData(viewModel: ViewModel<M>) {
        refreshProfile()
    }

    private fun refreshProfile() {
        profile_email.text = vm.email
        profile_name.text = if (vm.name.isBlank()) "Anonymous" else vm.name
        profile_location.text = vm.country
        profile_about.text = if (vm.description.isBlank()) "No information provided" else vm.description
        profile_image.setImage(vm.imageUrl)
    }
}