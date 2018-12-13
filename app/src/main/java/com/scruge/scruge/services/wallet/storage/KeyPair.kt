package com.scruge.scruge.services.wallet.storage

import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.EosPublicKey

class KeyPair(val privateKey: EosPrivateKey) {

    val publicKey: EosPublicKey = privateKey.publicKey
}