package com.scruge.scruge.view.views

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.view.setHidden
import com.scruge.scruge.services.eos.EosName
import com.scruge.scruge.viewmodel.resources.ResourcesVM
import com.ysoftware.mvvm.single.ViewModel
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.view_wallet_resources.view.*

class WalletResourcesView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        RelativeLayout(context, attrs, defStyleAttr), ViewModelDelegate {

    private val vm = ResourcesVM()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_wallet_resources, this, true)
        setup()
    }

    fun hideRAM(value:Boolean) {
        res_ram.setHidden(value)
    }

    fun hideControls(value:Boolean) {
        res_controls.setHidden(value)
    }

    private fun setup() {
        vm.delegate = this

        res_cpu_progress.mode = ProgressView.Mode.progress
        res_net_progress.mode = ProgressView.Mode.progress
        res_ram_progress.mode = ProgressView.Mode.progress

        res_cpu_progress.showLabels = false
        res_net_progress.showLabels = false
        res_ram_progress.showLabels = false

        res_cpu_progress.tintColor = ContextCompat.getColor(context, R.color.cpu)
        res_net_progress.tintColor = ContextCompat.getColor(context, R.color.net)
        res_ram_progress.tintColor = ContextCompat.getColor(context, R.color.ram)

        res_cpu_progress.backColor = ContextCompat.getColor(context, R.color.cpu_background)
        res_net_progress.backColor = ContextCompat.getColor(context, R.color.net_background)
        res_ram_progress.backColor = ContextCompat.getColor(context, R.color.ram_background)
    }

    var accountName: EosName? = null
        set(value) {
            field = value
            vm.accountName = value
            vm.load()
        }

    var buyRamBlock:(()->Unit)? = null
        set(value) {
            field = value
            res_ram_button.setOnClickListener { value?.invoke() }
        }

    var stakeBlock:(()->Unit)? = null
        set(value) {
            field = value
            res_stake_button.setOnClickListener { value?.invoke() }
        }

    private fun updateViews() {
        res_cpu_progress.value = vm.cpuUsedValue
        res_cpu_progress.firstGoal = vm.cpuLimitValue
        res_net_progress.value = vm.netUsedValue
        res_net_progress.firstGoal = vm.netLimitValue
        res_ram_progress.value = vm.ramUsedValue
        res_ram_progress.firstGoal = vm.ramLimitValue

        res_cpu_staked.text = vm.cpuWeight
        res_net_staked.text = vm.netWeight

        res_cpu_label.text = vm.cpu
        res_net_label.text = vm.net
        res_ram_label.text = vm.ram
    }

    override fun <M : Comparable<M>> didUpdateData(viewModel: ViewModel<M>) {
        (context as? Activity)?.runOnUiThread {
            updateViews()
        }
    }
}