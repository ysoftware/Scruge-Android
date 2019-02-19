package com.scruge.scruge.view.ui.bounty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.services.Service
import kotlinx.android.synthetic.main.fragment_bounty_bounty.*

class BountyFragment: NavigationFragment() {

    lateinit var bountyVM: BountyVM
    lateinit var projectVM: ProjectVM

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bounty_bounty, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        setupActions()
    }

    private fun setupActions() {
        submit_button.setOnClickListener {
            Service.presenter.presentSubmitFragment(this, bountyVM, projectVM)
        }
    }

    private fun setupViews() {
        val vm = bountyVM
        title = projectVM.name

        submit_button.text = R.string.do_submit.string()

        bounty_name.text = vm.name
        bounty_dates.text = vm.dates
        bounty_rewards.text = vm.rewards
        bounty_description.text = vm.description
        bounty_rules.text = vm.rules
        bounty_details.text = vm.longerDescription
    }

}