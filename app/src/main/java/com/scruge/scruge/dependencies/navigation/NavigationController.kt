package com.scruge.scruge.dependencies.navigation

import android.transition.Slide
import android.view.Gravity

import com.scruge.scruge.view.views.NavigationBar

import java.util.Stack
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
    private var rootFragment: Fragment? = null

    var isVisible = true

    var navigationBar: NavigationBar? = null
        set(navigationBar) {
            field = navigationBar
            navigationBar?.delegate = this
            navigationBar?.isBackButtonHidden = shouldHideNavigationBarBackButton
            updateViews()
        }

    val topFragment: Fragment? get() = if (fragmentStack.size == 0) rootFragment else fragmentStack.peek()

    // METHODS

    fun replaceRoot(fragment: Fragment) {
        val oldFragment = rootFragment
        rootFragment = fragment

        manager.executePendingTransactions()
        fragmentStack = Stack()
        manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        manager.beginTransaction().replace(containerId, fragment).commitAllowingStateLoss()
        manager.executePendingTransactions()

        if (fragment is NavigationFragment) {
            fragment.navigationController = this
        }

        update(oldFragment, fragment)
        updateViews()
    }

    fun popToRoot() {
        rootFragment?.let { replaceRoot(it) }
    }

    fun replaceTop(fragment: Fragment) {
        if (navigateBack()) {
            navigateTo(fragment)
        }
        else {
            replaceRoot(fragment)
        }
    }

    fun navigateTo(fragment: Fragment) {
        if (rootFragment == null) { return }

        val oldFragment = rootFragment
        if (android.os.Build.VERSION.SDK_INT > 21) {
            fragment.enterTransition = Slide(Gravity.END)
            fragment.exitTransition = Slide(Gravity.START)
        }

        manager.executePendingTransactions()
        fragmentStack.push(fragment)
        resetClickable()
        manager.beginTransaction().add(containerId, fragment).addToBackStack(null).commitAllowingStateLoss()
        manager.executePendingTransactions()

        if (fragment is NavigationFragment) {
            fragment.navigationController = this
        }

        update(oldFragment, fragment)
        updateViews()
    }

    fun navigateBack(): Boolean {
        if (fragmentStack.isEmpty()) { return false }

        val oldFragment = fragmentStack.pop()
        val fragment = topFragment!!

        manager.executePendingTransactions()
        manager.popBackStackImmediate(null, 0)
        manager.executePendingTransactions()

        if (fragment is NavigationFragment) {
            fragment.navigationController = this
        }

        update(oldFragment, fragment)
        updateViews()
        return true
    }

    fun onBackPressed(): Boolean {
        val value = (topFragment as? OnBackPressedListener)?.onBackPressedHandled() ?: return navigateBack()
        return if (!value) { navigateBack() } else { value }
    }

    fun updateViews() {
        setBackButtonHidden(fragmentStack.size == 0)
        (topFragment as? NavigationFragment)?.let {
            navigationBar
                    ?.setTitle(it.title)
                    ?.setupRightActionButton(it.navigationBarButton)
                    ?.setupRightActionButton2(it.navigationBarButton2)
                    ?.setHidden(it.shouldHideNavigationBar)
        }
    }

    // PRIVATE

    private fun resetClickable() {
        val top = topFragment
        for (fragment in fragmentStack) {
            var clickable = false
            if (fragment === top) {
                clickable = true
            }
            fragment.view?.isClickable = clickable
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
