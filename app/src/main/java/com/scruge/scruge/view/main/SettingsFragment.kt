package com.scruge.scruge.view.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.services.Service
import com.scruge.scruge.viewmodel.profile.ProfileVM
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment: NavigationFragment(), ArrayViewModelDelegate {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = R.string.title_settings.string()
        setupActions()
    }

    var profileVM: ProfileVM? = null

    private fun setupActions() {
        settings_sign_out.setOnClickListener {
            (activity as? TabbarActivity)?.selectTab(0)
            Service.tokenManager.removeToken()
            navigationController?.popToRoot()
        }

        settings_edit_profile.setOnClickListener {
            profileVM?.let { Service.presenter.presentProfileEditFragment(this, it) }
        }
    }
}