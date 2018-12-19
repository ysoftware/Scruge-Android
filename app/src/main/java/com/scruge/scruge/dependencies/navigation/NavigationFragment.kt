package com.scruge.scruge.dependencies.navigation

import androidx.fragment.app.Fragment
import com.scruge.scruge.view.views.NavigationBarButton

open class NavigationFragment : Fragment() {

    var navigationController: NavigationController? = null

    open fun viewWillAppear() {}

    open fun viewDidAppear() {}

    open fun viewWillDisappear() {}

    open fun viewDidDisappear() {}

    open var title:String = ""
        set(value) {
            field = value
            navigationController?.refreshNavigationBar()
        }

    var navigationBarButton:NavigationBarButton? = null
        set(value) {
            field = value
            navigationController?.refreshNavigationBar()
        }

    var navigationBarButton2:NavigationBarButton? = null
        set(value) {
            field = value
            navigationController?.refreshNavigationBar()
        }
}