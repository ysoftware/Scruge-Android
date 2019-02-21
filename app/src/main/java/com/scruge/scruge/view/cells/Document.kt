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

    private lateinit var adapter:Adapter

    fun setup(vm:DocumentAVM):DocumentsCell {
        itemView.documents_recycler_view.setupForVerticalLayout()
        adapter = Adapter(vm)
        itemView.documents_recycler_view.adapter = adapter
        return this
    }

    fun tap(tap: (DocumentVM)->Unit): DocumentsCell {
        adapter.tap = tap
        return this
    }

    class Adapter(val vm:DocumentAVM): RecyclerView.Adapter<Adapter.ViewHolder>() {

        lateinit var tap:(DocumentVM)->Unit

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