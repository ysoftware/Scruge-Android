package com.scruge.scruge.view.ui.authprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.main.TabbarActivity
import com.scruge.scruge.viewmodel.profile.ProfileVM
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment: NavigationFragment() {

    private var vm = ProfileVM()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        profile_signout.setOnClickListener {
            Service.tokenManager.removeToken()
            (activity as? TabbarActivity)?.selectTab(0)
        }

        profile_edit.setOnClickListener {
            Service.presenter.presentProfileEditFragment(this, vm)
        }
    }

    override fun onResume() {
        super.onResume()

        vm.load()
    }
}