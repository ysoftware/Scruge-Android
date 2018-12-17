package com.scruge.scruge.dependencies.navigation

import androidx.fragment.app.Fragment

open class NavigationFragment : Fragment() {

    var navigationController: NavigationController? = null

    open fun viewWillAppear() {}

    open fun viewDidAppear() {}

    open fun viewWillDisappear() {}

    open fun viewDidDisappear() {}
}