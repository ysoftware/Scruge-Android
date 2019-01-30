package com.scruge.scruge.view.views

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.RelativeLayout
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.view.alert
import com.scruge.scruge.dependencies.view.askForInput
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.wallet.storage.LocalAccount
import kotlinx.android.synthetic.main.view_wallet_data.view.*

class WalletData(context: Context, attrs: AttributeSet?, defStyleAttr:Int):
    RelativeLayout(context, attrs, defStyleAttr) {

        constructor(context: Context) : this(context, null)
        constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

        init {
            LayoutInflater.from(context).inflate(R.layout.view_wallet_data, this, true)
        }

    private var wallet:LocalAccount? = null
    private var unlocked = false

    fun lock() {
        makeSecure(false)
        updateViews()
    }

    fun updateViews() {
        makeSecure(false)
        wallet_data_private_key.text = "5••••••••••••••••••••••••••••••••••••••••••••••••••"

        wallet = Service.wallet.getAccount()
        wallet_data_public_key.text = wallet?.publicKey.toString()

        val wallet = this.wallet ?: return

        wallet_data_copy_private.setOnClickListener {
            if (unlocked) {
                (context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let { clipboard ->
                    val clip = ClipData.newPlainText(R.string.title_private_key.string(), wallet_data_private_key.text)
                    clipboard.primaryClip = clip
                    context?.alert(R.string.alert_copied_to_clipboard.string())
                }
            }
            else {
                (context as? Activity)?.askForInput(R.string.title_export_private_key.string(),
                                                    R.string.label_export_private_key.string(),
                                                    R.string.hint_wallet_password.string(),
                                                    true,
                                                    R.string.do_unlock_wallet.string()) { input ->
                    input?.let {
                        wallet.retrievePrivateKey(input) { key ->
                            key?.let {
                                makeSecure(true)

                                wallet_data_private_key.text = it.toString()
                                context?.alert(R.string.alert_private_key_warning.string())
                            } ?: context?.alert(R.string.error_incorrectPassword.string())
                        }
                    }
                }
            }
        }

        wallet_data_copy_public.setOnClickListener {
            (context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let { clipboard ->
                val clip = ClipData.newPlainText(R.string.title_public_key.string(), 
                                                 wallet.publicKey.toString())
                clipboard.primaryClip = clip
                context?.alert(R.string.alert_copied_to_clipboard.string())
            }
        }
    }

    private fun makeSecure(value:Boolean) {
        unlocked = value
        val flags = if (value) WindowManager.LayoutParams.FLAG_SECURE else 0
        (context as? Activity)?.window?.setFlags(flags, WindowManager.LayoutParams.FLAG_SECURE)
    }
}