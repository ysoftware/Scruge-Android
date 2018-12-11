package com.scruge.scruge.view.main

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationController
import com.scruge.scruge.services.Service

class AuthActivity: AppCompatActivity() {

    var authCompletionBlock:((Boolean)->Unit)? = null
    var didLogIn = false

    private val navigationController = NavigationController(supportFragmentManager, R.id.auth_content)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        supportActionBar?.hide()

        // present initial fragment
        authCompletionBlock = Service.presenter.setupAuth(navigationController)
    }

    override fun onDestroy() {
        super.onDestroy()
        authCompletionBlock?.invoke(didLogIn)
    }
}