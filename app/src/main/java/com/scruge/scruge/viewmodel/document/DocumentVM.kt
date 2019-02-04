package com.scruge.scruge.viewmodel.document

import com.scruge.scruge.model.entity.Document
import com.ysoftware.mvvm.single.ViewModel

class DocumentVM(model: Document?) : ViewModel<Document>(model) {

    val name get() = model?.name?.capitalize() ?: ""

    val documentUrl get() = model?.url ?: ""
}