package com.scruge.scruge.services.eos

import com.memtrip.eos.abi.writer.*
import com.memtrip.eos.abi.writer.bytewriter.DefaultByteWriter
import com.memtrip.eos.abi.writer.compression.CompressionFactory
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.ChainResponse
import com.memtrip.eos.chain.actions.transaction.AbiBinaryGenTransactionWriter
import com.memtrip.eos.chain.actions.transaction.ChainTransaction
import com.memtrip.eos.chain.actions.transaction.TransactionContext
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.chain.actions.transaction.transfer.actions.TransferArgs
import com.memtrip.eos.core.hex.DefaultHexWriter
import com.memtrip.eos.http.rpc.ChainApi
import com.memtrip.eos.http.rpc.model.transaction.response.TransactionCommitted
import io.reactivex.Single
import java.util.Arrays.asList

@Abi
data class ScrugeVote(val eosAccount:String,
                      val userId:Int,
                      val campaignId:Int,
                      val vote:Boolean): AbiConvertible {

    override fun toBytes(): ByteArray {
        val writer = DefaultByteWriter(512)
        writer.putAccountName(eosAccount)
        writer.putLong(userId.toLong())
        writer.putLong(campaignId.toLong())
        writer.putInt(if (vote) 1 else 0)
        return writer.toBytes()
    }
}