package com.scruge.scruge.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.NavigationController
import com.scruge.scruge.services.Service
import kotlinx.android.synthetic.main.activity_main.*

class TabbarActivity : AppCompatActivity() {

    var selectedTab = 0; private set
    val navigationControllers =
            listOf(NavigationController(supportFragmentManager, R.id.container_view_featured),
                   NavigationController(supportFragmentManager, R.id.container_view_activity),
                   NavigationController(supportFragmentManager, R.id.container_view_wallet),
                   NavigationController(supportFragmentManager, R.id.container_view_profile))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        Service.presenter.setupMainTabs(this)
        setupTabbar()
    }

    private fun setupTabbar() {
        selectTab(0)
        tabbar.setOnNavigationItemSelectedListener {
            selectTab(it.order)
            true
        }
    }

    private fun selectTab(index:Int) {
        selectedTab = index
        for (i in 0 until navigationControllers.size) {
            val view = findViewById<View>(navigationControllers[i].containerId)
            view.visibility = if (index == i) View.VISIBLE else View.GONE
        }
    }
}
