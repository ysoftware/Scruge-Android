package com.scruge.scruge.services.api.model

import android.os.Build
import com.google.gson.annotations.Expose
import com.scruge.scruge.model.entity.Profile

// response

data class LoginResponse(val result: Int, val token:String)

data class UserIdResponse(@Expose val userId:Int)

data class ProfileResponse(val profile: Profile)

// request

data class EmailRequest(val email:String)

data class LoginRequest(val login:String)

data class AuthRequest(val login:String, val password:String, val pushNotificationToken:String?) {

    val device = "Android ${Build.VERSION.RELEASE}"
}

data class RegisterRequest(val login:String, val password:String)

data class ProfileRequest(val name:String, val country:String, val description:String)