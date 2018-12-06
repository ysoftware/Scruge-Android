package com.scruge.scruge.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.NavigationController
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.api.Api

class TabbarActivity : AppCompatActivity() {

    val navigationController = NavigationController(supportFragmentManager, R.id.container_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        Service.presenter.setupMainTabs(this)
    }
}
