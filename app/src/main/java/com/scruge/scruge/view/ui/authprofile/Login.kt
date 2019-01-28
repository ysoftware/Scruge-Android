package com.scruge.scruge.view.ui.authprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.isValidEmail
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.askForInput
import com.scruge.scruge.dependencies.view.hideKeyboard
import com.scruge.scruge.model.error.AuthError
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.main.AuthActivity
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: NavigationFragment() {

    // PROPERTIES

    private var isWorking = false

    private val email get() = login_email?.text?.toString() ?: ""
    private val password get() = login_password?.text?.toString() ?: ""

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        setupActions()
    }

    private fun setupViews() {
        login_button.title = "Log In"
    }

    private fun setupActions() {
        login_close_button.setOnClickListener {
            hideKeyboard()
            activity?.finish()
        }

        login_button.click { login() }
        view?.setOnClickListener { hideKeyboard() }
        login_signup.setOnClickListener { Service.presenter.replaceWithRegisterFragment(this) }
        login_privacy.setOnClickListener { Service.presenter.presentPrivacyPolicy(this) }

        login_forgot.setOnClickListener {
            hideKeyboard()

            askForInput("Reset password", "Enter your email", "Email…", false, "Send", email) { string ->
                string?.let {
                    if (it.isValidEmail()) {
                        Service.api.resetPassword(string) {
                            it.onSuccess {
                                    ErrorHandler.error(it.result)?.let { alert(it) }
                                            ?: alert("Check your email address for the letter with password recovery instructions")
                            }.onFailure {
                                alert(it)
                            }
                        }
                    }
                    else {
                        alert(AuthError.invalidEmail)
                    }
                }
            }
        }
    }

    // ACTIONS

    private fun login() {
        if (isWorking || !validate()) { return }

        isWorking = true
        Service.api.login(email, password) { result ->
            isWorking = false

            result.onSuccess {
                Service.tokenManager.save(it.token)

                hideKeyboard()
                (activity as? AuthActivity)?.didLogIn = true
                activity?.finish()
            }.onFailure {
                alert(it)
            }
        }
    }

    private fun validate():Boolean {
        if (email.isEmpty()) {
            alert("Enter your email")
            return false
        }

        if (password.isEmpty()) {
            alert("Enter your password")
            return false
        }

        if (!email.isValidEmail()) {
            alert("Email is not valid")
            return false
        }

        if (password.length <= 6) {
            alert("Password is too short")
            return false
        }

        return true
    }
}