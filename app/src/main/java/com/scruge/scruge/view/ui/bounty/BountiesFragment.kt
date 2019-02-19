package com.scruge.scruge.view.ui.bounty

import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.single.ViewModelDelegate

class BountiesFragment : NavigationFragment(), ViewModelDelegate, ArrayViewModelDelegate {

    private lateinit var bountyAVM: BountyAVM
    lateinit var projectVM: ProjectVM
}