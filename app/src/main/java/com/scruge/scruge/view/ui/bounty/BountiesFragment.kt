package com.scruge.scruge.view.ui.bounty

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.single.ViewModelDelegate

class BountiesFragment : NavigationFragment(), ViewModelDelegate, ArrayViewModelDelegate {

    private lateinit var bountyAVM: BountyAVM
    lateinit var projectVM: ProjectVM

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bounty_bounties, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        setupActions()
    }

    private fun setupActions() {

    }

    private fun setupViews() {

    }

}