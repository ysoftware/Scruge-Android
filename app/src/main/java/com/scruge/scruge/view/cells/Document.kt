package com.scruge.scruge.view.cells

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.viewmodel.document.DocumentAVM
import com.scruge.scruge.viewmodel.document.DocumentVM
import kotlinx.android.synthetic.main.cell_document.view.*
import kotlinx.android.synthetic.main.cell_documents.view.*

class DocumentsCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private lateinit var tap:(DocumentVM)->Unit
    private lateinit var vm:DocumentAVM

    fun setup(vm:DocumentAVM):DocumentsCell {
        this.vm = vm
        itemView.documents_recycler_view.setupForVerticalLayout()
        itemView.documents_recycler_view.adapter = Adapter(vm, tap)
        return this
    }

    fun tap(tap: (DocumentVM)->Unit): DocumentsCell {
        this.tap = tap
        return this
    }

    class Adapter(val vm:DocumentAVM, val tap:(DocumentVM)->Unit): RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context)
                                      .inflate(R.layout.cell_document, parent, false))
        }

        override fun getItemCount(): Int {
            return vm.numberOfItems
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = vm.item(position)
            holder.itemView.document_title.text = item.name
            holder.itemView.setOnClickListener { tap(item) }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}