package com.scruge.scruge.view.ui.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.setImage
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.Member
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.cells.SocialAdapter
import kotlinx.android.synthetic.main.fragment_member_profile.*


class MemberProfileFragment : NavigationFragment() {

    lateinit var member: Member

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_member_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupTable()
        refreshProfile()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()
        setupNavigationBar()
    }

    private fun setupTable() {
        member_social_recycler_view.setupForVerticalLayout()
        val adapter = SocialAdapter(member.social)
        adapter.tap = {
            Service.presenter.presentBrowser(this, it.url)
        }
        member_social_recycler_view.adapter = adapter
    }

    private fun setupNavigationBar() {
        shouldHideNavigationBar = true
        title = R.string.title_team_member.string()
    }

    private fun refreshProfile() {
        member_profile_name.text = member.name
        member_profile_position.text = member.position
        member_profile_about.text = member.description
        member_profile_image.setImage(member.imageUrl)
    }
}