package com.scruge.scruge.view.ui.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.Settings
import com.scruge.scruge.viewmodel.account.AccountAVM
import com.scruge.scruge.viewmodel.account.AccountVM
import com.ysoftware.mvvm.array.ArrayViewModel
import com.ysoftware.mvvm.array.ArrayViewModelDelegate
import com.ysoftware.mvvm.array.Query
import com.ysoftware.mvvm.array.Update
import com.ysoftware.mvvm.single.ViewModel
import kotlinx.android.synthetic.main.activity_wallet_select.*
import kotlinx.android.synthetic.main.cell_account.view.*

class WalletPickerActivity: AppCompatActivity(), ArrayViewModelDelegate {

    private val vm = AccountAVM()
    private val adapter = Adapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet_select)
        supportActionBar?.hide()

        vm.delegate = this
        vm.reloadData()
        wallet_select_recycler_view.setupForVerticalLayout()
        wallet_select_recycler_view.adapter = adapter
        wallet_select_done.setOnClickListener {
            val index = adapter.selected
            val item = if (vm.numberOfItems > index && index != -1) vm.item(index) else null
            item?.let { Service.settings.set(Settings.Setting.selectedAccount, it.displayName) }
            setResult(0)
            finish()
        }
    }

    override fun <M : Comparable<M>, VM : ViewModel<M>, Q : Query> didUpdateData(
            arrayViewModel: ArrayViewModel<M, VM, Q>, update: Update) {
        runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }

    class Adapter(val activity:WalletPickerActivity): RecyclerView.Adapter<Adapter.ViewHolder>() {

        var selected = -1

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewwHolder {
            return Adapter.ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.cell_account, parent, false))
        }

        override fun getItemCount(): Int {
            return activity.vm.numberOfItems
        }

        override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
            val vm = activity.vm.item(position)
            holder.setup(vm, selected == position)
            holder.itemView.setOnClickListener {
                selected = holder.adapterPosition
                notifyDataSetChanged()
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun setup(vm: AccountVM, selected: Boolean) {
                itemView.account_name.text = vm.displayName
                itemView.account_checkmark_image.visibility = if (selected) View.VISIBLE else View.GONE
                itemView.account_checkmark.setBackgroundResource(
                        if (selected) R.drawable.checkmark_selected else R.drawable.checkmark_unselected)
            }
        }
    }
}