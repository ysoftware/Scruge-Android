package com.scruge.scruge.viewmodel.document

import com.scruge.scruge.model.entity.Document
import com.ysoftware.mvvm.array.SimpleArrayViewModel

class DocumentAVM(list:List<Document>): SimpleArrayViewModel<Document, DocumentVM>() {

    init {
        setData(list.map { DocumentVM(it) })
    }

    override fun fetchData(block: (Result<Collection<DocumentVM>>) -> Unit) {
        // no-op
    }
}