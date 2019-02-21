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
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.error.AuthError
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.main.AuthActivity
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: NavigationFragment() {

    // PROPERTIES

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
        login_button.title = R.string.do_log_in.string()
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

            askForInput(R.string.title_reset_password.string(),
                        R.string.label_enter_your_email.string(),
                        R.string.hint_email_address.string(),
                        false,
                        R.string.do_send.string(),
                        email) { string ->
                string?.let {
                    if (it.isValidEmail()) {
                        Service.api.resetPassword(string) {
                            it.onSuccess {
                                    ErrorHandler.error(it.result)?.let { alert(it) }
                                            ?: alert(R.string.label_reset_password_message.string())
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
        if (!validate()) { return }

        login_button.isBusy = true
        Service.api.login(email, password) { result ->
            login_button.isBusy = false

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
        if (email.isBlank()) {
            alert(R.string.error_login_enter_email.string())
            return false
        }

        if (password.isEmpty()) {
            alert(R.string.error_login_enter_password.string())
            return false
        }

        if (!email.isValidEmail()) {
            alert(AuthError.invalidEmail)
            return false
        }

        if (password.length <= 6) {
            alert(AuthError.incorrectPasswordLength)
            return false
        }

        return true
    }
}