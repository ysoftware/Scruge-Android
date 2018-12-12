package com.scruge.scruge.viewmodel.profile

import android.graphics.Bitmap
import com.scruge.scruge.model.entity.Profile
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.model.error.ScrugeError
import com.scruge.scruge.services.Service
import com.ysoftware.mvvm.single.ViewModel

class ProfileVM(model: Profile? = null) : ViewModel<Profile>(model) {

    val email get() = model?.login ?: ""

    val imageUrl get() = model?.imageUrl

    val description get() = model?.description ?: ""

    val name get() = model?.name ?: ""

    val country get() = model?.country ?: ""

    // METHODS

    fun load() {
        Service.api.getProfile { result ->
            model = result.getOrNull()?.profile
            notifyUpdated()
        }
    }

    companion object {

        fun updateProfile(name: String?, country: String?, description: String?, image: Bitmap?,
                          completion: (ScrugeError?) -> Unit) {
            Service.api.updateProfile(name ?: "", country ?: "", description ?: "") { result ->
                result.onSuccess {

                    val error = ErrorHandler.error(it.result)
                    if (error != null) {
                        completion(error)
                        return@onSuccess
                    }

                    if (image == null) {
                        completion(null)
                    }
                    else {
                        Service.api.updateProfileImage(image) { result ->
                            completion(ErrorHandler.error(result.exceptionOrNull()))
                        }
                    }
                }.onFailure {
                    completion(ErrorHandler.error(it))
                }
            }
        }
    }
}