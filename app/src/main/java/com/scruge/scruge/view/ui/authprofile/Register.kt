package com.scruge.scruge.view.ui.authprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.isValidEmail
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.hideKeyboard
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.error.AuthError
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.services.Service
import com.scruge.scruge.view.main.AuthActivity
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment: NavigationFragment() {

    // PROPERTIES

    private var isWorking = false

    private val email get() = register_email?.text?.toString() ?: ""
    private val password get() = register_password?.text?.toString() ?: ""
    private val passwordConfirm get() = register_confirm?.text?.toString() ?: ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        setupActions()
    }

    private fun setupViews() {
        register_button.title = R.string.title_sign_up.string()
    }

    private fun setupActions() {
        register_close_button.setOnClickListener {
            hideKeyboard()
            activity?.finish()
        }

        register_button.click { signUp() }
        view?.setOnClickListener { hideKeyboard() }
        register_login.setOnClickListener { Service.presenter.replaceWithLoginFragment(this) }
        register_privacy.setOnClickListener { Service.presenter.presentPrivacyPolicy(this) }
    }

    private fun signUp() {
        if (isWorking || !validate()) { return }

        isWorking = true
        Service.api.signUp(email, password) { result ->
            isWorking = false
            result.onSuccess {
                ErrorHandler.error(it.result)?.let {
                    alert(it)
                } ?: finishLogin(email, password)
            }.onFailure {
                alert(it)
            }
        }
    }

    private fun finishLogin(email:String, password:String) {
        isWorking = true
        Service.api.login(email, password) { result ->
            isWorking = false

            result.onSuccess {
                Service.tokenManager.save(it.token)
                (activity as? AuthActivity)?.didLogIn = true
                hideKeyboard()
                Service.presenter.presentProfileSetupFragment(this)
            }.onFailure {
                alert(it)
            }
        }
    }

    private fun validate():Boolean {
        if (password != passwordConfirm) {
            alert(R.string.error_register_passwords_do_not_match.string())
            return false
        }

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