package com.scruge.scruge.view.ui.authprofile

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationController
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.services.Service
import com.scruge.scruge.viewmodel.profile.ProfileVM
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import com.theartofdev.edmodo.cropper.CropImage
import android.content.Intent
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import com.scruge.scruge.dependencies.view.*
import com.scruge.scruge.view.main.TabbarActivity

class EditProfileFragment: NavigationFragment(), NavigationController.OnBackPressedListener {

    // PROPERTIES

    var editingProfile: ProfileVM? = null
    private var selectedImageUri: Uri? = null
    private var cropImageUri: Uri? = null

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupViews()
        setupActions()
        setupEditing()
    }

    override fun viewDidAppear() {
        super.viewDidAppear()

        setupNavigationBar()
    }

    private fun setupViews() {
        edit_profile_button.title = if (editingProfile == null) R.string.do_create_profile.string()
        else R.string.do_update_profile.string()
    }

    private fun setupActions() {
        view?.setOnClickListener { hideKeyboard() }
        edit_profile_button.click { save() }
        edit_profile_image.setOnClickListener { Service.presenter.presentImagePickerActivity(this) }
    }

    private fun setupNavigationBar() {
        (activity as? TabbarActivity)?.tabbarHidden = true
        shouldHideNavigationBar = true
    }

    private fun setupEditing() {
        editingProfile?.let { vm ->
            edit_profile_name.setText(if (vm.name.isBlank()) R.string.label_anonymous.string() else vm.name)
            edit_profile_location.setText(vm.country)
            edit_profile_about.setText(vm.description)
            edit_profile_image.setImage(vm.imageUrl, hideOnFail = false)
        }
    }

    // ACTIONS

    private fun save() {
        val name = edit_profile_name.text.toString().trim()
        val country = edit_profile_location.text.toString().trim()
        val description = edit_profile_about.text.toString().trim()

        ProfileVM.updateProfile(name, country, description, selectedImageUri) { error ->
            if (error != null) {
                alert(error)
                return@updateProfile
            }

            if (editingProfile == null) {
                activity?.finish()
            }
            else {
                navigationController?.navigateBack()
            }
        }
    }

    override fun onBackPressedHandled(): Boolean {
        if (editingProfile != null) { return false }

        ask(question = R.string.label_sure_to_quit.string()) { reply ->
            if (reply) {
                Service.tokenManager.removeToken()
                navigationController?.navigateBack()
            }
        }
        return true
    }

    // IMAGE PICKING

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val context = activity ?: return

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = CropImage.getPickImageResultUri(context, data)

            if (CropImage.isReadExternalStoragePermissionsRequired(context, imageUri)) {
                cropImageUri = imageUri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                   CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE)
            }
            else {
                Service.presenter.presentCropImageActivity(this, imageUri)
            }
        }
        else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            val result = CropImage.getActivityResult(data)
            selectedImageUri = result.uri
            edit_profile_image.setImageURI(selectedImageUri)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val uri = cropImageUri ?: return
                Service.presenter.presentCropImageActivity(this, uri)
            }
        }
    }
}