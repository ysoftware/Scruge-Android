package com.scruge.scruge.view.views

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.view.setHidden
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.services.eos.EosName
import com.scruge.scruge.viewmodel.transaction.ActionVM
import com.scruge.scruge.viewmodel.transaction.ActionsAVM
import com.scruge.scruge.viewmodel.transaction.ActionsQuery
import com.ysoftware.mvvm.array.ArrayViewModel
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.array.Query
import com.ysoftware.mvvm.array.Update
import com.ysoftware.mvvm.single.ViewModel
import kotlinx.android.synthetic.main.cell_transaction.view.*

class WalletTransactionsView(context: Context, attrs: AttributeSet?, defStyleAttr:Int):
        RelativeLayout(context, attrs, defStyleAttr), ArrayViewModelDelegate {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private var vm:ActionsAVM? = null
    private val adapter = Adapter(this)
    private val recyclerView = RecyclerView(context)

    init {
        addView(recyclerView)
        recyclerView.setupForVerticalLayout()
        recyclerView.adapter = adapter
    }

    var accountName: EosName? = null
        set(value) {
            field = value

            if (value != null) {
                vm = ActionsAVM(value)
                vm?.delegate = this
                vm?.query = ActionsQuery()
                vm?.reloadData()
            }
            else {
                vm = null
                adapter.notifyDataSetChanged()
            }
        }

    // DELEGATE

    override fun <M : Comparable<M>, VM : ViewModel<M>, Q : Query> didUpdateData(
            arrayViewModel: ArrayViewModel<M, VM, Q>, update: Update) {
        (context as? Activity)?.runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }

    class Adapter(private val v: WalletTransactionsView): RecyclerView.Adapter<Adapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context)
                                      .inflate(R.layout.cell_transaction, parent, false))
        }

        override fun getItemCount(): Int {
            return v.vm?.numberOfItems ?: 0
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            v.vm?.item(position, true)?.let { holder.setup(it) }
        }

        class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            fun setup(vm: ActionVM) {
                val c = ContextCompat.getColor(itemView.context, vm.actionColor)
                itemView.cell_action_name.setTextColor(c)

                itemView.cell_action_name.text = vm.actionName
                itemView.cell_action_time.text = vm.time
                itemView.cell_action_description.text = vm.actionDescription
                itemView.cell_action_details.text = vm.actionDetails

                itemView.cell_action_description.setHidden(vm.actionDescription == null)
                itemView.cell_action_details.setHidden(vm.actionDetails == null)
            }
        }
    }
}

