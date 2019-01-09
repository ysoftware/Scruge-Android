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

        unlocked = false
        wallet_data_private_key.text = "5••••••••••••••••••••••••••••••••••••••••••••••••••"

        wallet = Service.wallet.getAccount()
        wallet_data_public_key.text = wallet?.publicKey.toString()

        val wallet = this.wallet ?: return

        wallet_data_copy_private.setOnClickListener {
            if (unlocked) {
                (context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let { clipboard ->
                    val clip = ClipData.newPlainText("EOS Private key", wallet_data_private_key.text)
                    clipboard.primaryClip = clip
                    context?.alert("Copied to clipboard")
                }
            }
            else {
                context?.askForInput("Export private key",
                                     "Enter your wallet password",
                                     "Wallet password…",
                                     true,
                                     "Unlock") { input ->
                    input?.let {
                        wallet.retrievePrivateKey(input) { key ->
                            key?.let {
                                unlocked = true
                                makeSecure(true)

                                wallet_data_private_key.text = it.toString()
                                context?.alert("Be careful not to share your private key with anyone!")
                            } ?: context?.alert("Incorrect password")
                        }
                    }
                }
            }
        }

        wallet_data_copy_public.setOnClickListener {
            (context?.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let { clipboard ->
                val clip = ClipData.newPlainText("EOS Public key", wallet.publicKey.toString())
                clipboard.primaryClip = clip
                context?.alert("Copied to clipboard")
            }
        }
    }

    private fun makeSecure(value:Boolean) {
        val flags = if (value) WindowManager.LayoutParams.FLAG_SECURE else 0
        (context as? Activity)?.window?.setFlags(flags, WindowManager.LayoutParams.FLAG_SECURE)
    }
}