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
import java.util.*
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

data class Token(val contract:EosName,
                 val symbol:String) {

    override fun toString(): String = "$contract $symbol"

    companion object {

        // todo change on the main net
        val Scruge = Token(EosName.create("eosio.token"), "SCR")

        val EOS = Token(EosName.create("eosio.token"), "EOS")

        val SYS = Token(EosName.create("eosio.token"), "SYS")

        val default = listOf(EOS) // Scruge

        fun from(string:String):Token? {
            val array = string.split(" ")
            if (array.size == 2) {
                val contract = EosName.from(array[0])
                if (contract != null) {
                    return Token(contract, array[1])
                }
            }
            return null
        }
    }

    override fun hashCode(): Int = Objects.hash(contract, symbol)

    override fun equals(other: Any?): Boolean {
        if (other is Token) { return other.contract == contract && other.symbol == symbol  }
        return super.equals(other)
    }
}