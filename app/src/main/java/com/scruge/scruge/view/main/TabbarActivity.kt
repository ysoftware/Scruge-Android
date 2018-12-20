package com.scruge.scruge.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationController
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.Dimension
import com.scruge.scruge.services.Service
import kotlinx.android.synthetic.main.activity_main.*

class TabbarActivity : AppCompatActivity() {

    private val navigationControllers =
            listOf(NavigationController(supportFragmentManager, R.id.container_view_featured),
                   NavigationController(supportFragmentManager, R.id.container_view_activity),
                   NavigationController(supportFragmentManager, R.id.container_view_wallet),
                   NavigationController(supportFragmentManager, R.id.container_view_profile))

    var selectedTab = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        navigationControllers.forEach { it.isVisible = false }
        Service.presenter.setupMainTabs(navigationControllers)
        setupTabbar()
    }

    private fun setupTabbar() {
        selectTab(0)
        tabbar.setOnNavigationItemSelectedListener {
            selectTab(it.order)
        }
    }

    private fun shouldSelectTab(index: Int):Boolean {
        val id = tabbar.menu.getItem(index).itemId

        // if opening profile but not logged in yet
        if (id == R.id.menu_profile) { // todo change when add messages
            if (!Service.tokenManager.hasToken) {
                Service.presenter.presentLoginActivity(this) { didLogIn ->
                    if (didLogIn) {
                        selectTab(3)
                    }
                }
                return false
            }
        }
        return true
    }

    fun selectTab(index:Int):Boolean {
        val shouldSelect = shouldSelectTab(index)
        if (shouldSelect && selectedTab != index) {
            val id = tabbar.menu.getItem(index).itemId
            val previousSelectedTab = selectedTab
            selectedTab = index
            tabbar.selectedItemId = id
            for (i in 0 until navigationControllers.size) {
                val nav = navigationControllers[i]
                val view = findViewById<View>(nav.containerId)
                if (index == i) {
                    view.visibility = View.VISIBLE
                    nav.isVisible = true
                    nav.navigationBar = navigation_bar

                    (nav.topFragment as? NavigationFragment)?.viewDidAppear()
                }
                else {
                    view.visibility = View.GONE
                    nav.isVisible = false

                    if (previousSelectedTab == i) {
                        (nav.topFragment as? NavigationFragment)?.viewDidDisappear()
                    }
                }
            }
        }
        return shouldSelect
    }

    var tabbarHidden = false
        set(value) {
            field = value

            (tabbar.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
                it.bottomMargin = if (value) -Dimension.dp(55).px else 0
            }
        }

    override fun onBackPressed() {
        if (!navigationControllers[selectedTab].onBackPressed()) {
            super.onBackPressed()
        }
    }
}