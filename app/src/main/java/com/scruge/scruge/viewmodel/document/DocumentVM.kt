package com.scruge.scruge.viewmodel.document

import android.net.Uri
import com.scruge.scruge.model.entity.Document
import com.ysoftware.mvvm.single.ViewModel
import java.lang.Exception

class DocumentVM(model: Document?) : ViewModel<Document>(model) {

    val name get() = model?.name?.capitalize() ?: ""

    val documentUrl get() = model?.url?.let { try { Uri.parse(it) } catch (ex:Exception) { null }}
}