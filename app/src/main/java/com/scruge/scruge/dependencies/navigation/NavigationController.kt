package com.scruge.scruge.dependencies.navigation

import android.transition.Slide
import android.view.Gravity

import com.scruge.scruge.view.views.NavigationBar

import java.util.Stack
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class NavigationController(private val manager: FragmentManager, val containerId: Int) :
        NavigationBar.Delegate {

    /// return true if handled, false to pass further
    interface OnBackPressedListener {
        fun onBackPressedHandled(): Boolean
    }

    // FRAGMENT

    private var shouldHideNavigationBarBackButton = true
    private var fragmentStack = Stack<Fragment>()
    private var currentFragment: Fragment? = null

    var isVisible = true

    var navigationBar: NavigationBar? = null
        set(navigationBar) {
            field = navigationBar
            navigationBar?.delegate = this
            navigationBar?.isBackButtonHidden = shouldHideNavigationBarBackButton
            refreshNavigationBar()
        }

    fun replaceWith(fragment: Fragment) {
        val oldFragment = currentFragment
        currentFragment = fragment

        manager.executePendingTransactions()
        fragmentStack = Stack()
        manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        manager.beginTransaction().replace(containerId, fragment).commitAllowingStateLoss()

        if (fragment is NavigationFragment) {
            fragment.navigationController = this
        }

        manager.executePendingTransactions()
        update(oldFragment, fragment)

        setBackButtonHidden(true)
        refreshNavigationBar()
    }

    fun navigateTo(fragment: Fragment) {
        val oldFragment = currentFragment
        if (android.os.Build.VERSION.SDK_INT > 21) {
            fragment.enterTransition = Slide(Gravity.END)
            fragment.exitTransition = Slide(Gravity.START)
        }

        if (currentFragment == null) { return }

        manager.executePendingTransactions()
        fragmentStack.push(fragment)
        resetClickable()
        manager.beginTransaction().add(containerId, fragment).addToBackStack(null).commitAllowingStateLoss()

        if (fragment is NavigationFragment) {
            fragment.navigationController = this
        }

        manager.executePendingTransactions()
        update(oldFragment, fragment)

        setBackButtonHidden(false)
        refreshNavigationBar()
    }

    fun navigateBack(): Boolean {
        val oldFragment = currentFragment
        if (fragmentStack.size > 0) {
            fragmentStack.pop()
        }

        val fragment = topFragment()
        if (fragment == null || manager.backStackEntryCount == 0) {
            return false
        }

        manager.executePendingTransactions()
        manager.popBackStackImmediate(null, 0)
        update(oldFragment, fragment)

        setBackButtonHidden(fragmentStack.size <= 1)
        refreshNavigationBar()
        return true
    }

    fun onBackPressed(): Boolean {
        val value = (topFragment() as? OnBackPressedListener)?.onBackPressedHandled() ?: return navigateBack()
        return if (!value) { navigateBack() } else { value }
    }

    @Nullable
    fun topFragment(): Fragment? {
        return if (fragmentStack.size == 0) {
            currentFragment
        }
        else fragmentStack.peek()
    }

    private fun resetClickable() {
        val top = topFragment()
        for (fragment in fragmentStack) {
            var clickable = false
            if (fragment === top) {
                clickable = true
            }
            fragment.view?.isClickable = clickable
        }
    }

    fun refreshNavigationBar() {
        (topFragment() as? NavigationFragment)?.let {
            navigationBar
                    ?.setTitle(it.title)
                    ?.setupRightActionButton(it.navigationBarButton)
                    ?.setupRightActionButton2(it.navigationBarButton2)
                    ?.setHidden(it.shouldHideNavigationBar)
        }
    }

    private fun setBackButtonHidden(value: Boolean) {
        shouldHideNavigationBarBackButton = value
        navigationBar?.isBackButtonHidden = value
    }

    // HELPER

    private fun update(oldFragment: Fragment?, fragment: Fragment) {
        if (!isVisible) { return }

        (fragment as? NavigationFragment)?.viewDidAppear() ?: fragment.onResume()
        (oldFragment as? NavigationFragment)?.viewDidDisappear() ?: oldFragment?.onPause()
    }

    override fun navigationBarDidClickBackButton(instance: NavigationBar) {
        navigateBack()
    }
}
