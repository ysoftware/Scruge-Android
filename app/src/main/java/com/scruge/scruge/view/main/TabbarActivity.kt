package com.scruge.scruge.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationController
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
            selectedTab = index
            tabbar.selectedItemId = id
            for (i in 0 until navigationControllers.size) {
                val view = findViewById<View>(navigationControllers[i].containerId)
                view.visibility = if (index == i) View.VISIBLE else View.GONE
            }
        }
        return shouldSelect
    }
}