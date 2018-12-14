package com.scruge.scruge.services.eos

import com.memtrip.eos.abi.writer.ByteWriter
import com.memtrip.eos.abi.writer.Squishable
import com.memtrip.eos.abi.writer.bytewriter.DefaultByteWriter
import com.memtrip.eos.abi.writer.compression.CompressionFactory
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.ChainResponse
import com.memtrip.eos.chain.actions.transaction.AbiBinaryGenTransactionWriter
import com.memtrip.eos.chain.actions.transaction.ChainTransaction
import com.memtrip.eos.chain.actions.transaction.TransactionContext
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.core.hex.DefaultHexWriter
import com.memtrip.eos.http.rpc.ChainApi
import com.memtrip.eos.http.rpc.model.transaction.response.TransactionCommitted
import io.reactivex.Single
import java.util.*

class Action(chainApi: ChainApi) : ChainTransaction(chainApi) {

    fun <M:AbiConvertible> push(contract: String,
                 action:String,
                 data: M,
                 permission:String = "active",
                 context: TransactionContext): Single<ChainResponse<TransactionCommitted>> {

        val bytes = data.toBytes()
        val compressed = CompressionFactory(CompressionType.NONE).create().compress(bytes)
        val bin = DefaultHexWriter().bytesToHex(compressed, 0, compressed.size, null)

        val auth = Arrays.asList(TransactionAuthorizationAbi(context.authorizingAccountName, permission))
        val actions = Arrays.asList(ActionAbi(contract, action, auth, bin))

        return push(context.expirationDate, actions, context.authorizingPrivateKey)
    }
}

interface AbiConvertible { fun toBytes():ByteArray }
