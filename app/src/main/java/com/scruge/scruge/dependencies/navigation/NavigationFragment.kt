package com.scruge.scruge.dependencies.navigation

import androidx.fragment.app.Fragment
import com.scruge.scruge.view.views.NavigationBarButton

open class NavigationFragment : Fragment() {

    var navigationController: NavigationController? = null

    open fun viewDidAppear() {}

    open fun viewDidDisappear() {}

    open var title:String = ""
        set(value) {
            field = value
            navigationController?.updateViews()
        }

    var navigationBarButton:NavigationBarButton? = null
        set(value) {
            field = value
            navigationController?.updateViews()
        }

    var navigationBarButton2:NavigationBarButton? = null
        set(value) {
            field = value
            navigationController?.updateViews()
        }

    var shouldHideNavigationBar = false
        set(value) {
            field = value
            navigationController?.updateViews()
        }
}