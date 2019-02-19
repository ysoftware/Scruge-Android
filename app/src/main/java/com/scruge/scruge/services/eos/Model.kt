package com.scruge.scruge.services.eos

import com.memtrip.eos.abi.writer.Abi
import com.memtrip.eos.abi.writer.bytewriter.DefaultByteWriter
import java.util.*

@Abi
data class Submission(val hunterName:String,
                      val providerName:String,
                      val proof:String,
                      val bountyId:Long): AbiConvertible {

    override fun toBytes(): ByteArray {
        val writer = DefaultByteWriter(512)
        writer.putAccountName(hunterName)
        writer.putAccountName(providerName)
        writer.putString(proof)
        writer.putLong(bountyId)
        return writer.toBytes()
    }
}

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