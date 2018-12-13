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
import com.memtrip.eos.core.hex.DefaultHexWriter
import com.memtrip.eos.http.rpc.ChainApi
import com.memtrip.eos.http.rpc.model.transaction.response.TransactionCommitted
import io.reactivex.Single
import java.util.Arrays.asList

@Abi
data class ScrugeVote(val eosAccount:String,
                      val userId:Int,
                      val campaignId:Int,
                      val vote:Boolean) {
    val getEosAccount:String @AccountNameCompress get() = eosAccount
    val getUserId:Int @IntCompress get() = userId
    val getCampaignId:Int @IntCompress get() = campaignId
    val getVote:Boolean @IntCompress get() = vote
}

class VoteTransaction(chainApi:ChainApi): ChainTransaction(chainApi) {

    fun vote(contract: String,
             args: ScrugeVote,
             transactionContext: TransactionContext): Single<ChainResponse<TransactionCommitted>> {

        return push(transactionContext.expirationDate,
                    asList(ActionAbi(contract,
                                     "transfer",
                                     asList(TransactionAuthorizationAbi(
                                             transactionContext.authorizingAccountName, "active")),
                                     getBin(args))),
                    transactionContext.authorizingPrivateKey)
    }

    private fun getBin(args: ScrugeVote): String {
        val byteWriter = DefaultByteWriter(512)
        val hexWriter = DefaultHexWriter()
        val gen = AbiBinaryGenTransactionWriter(CompressionType.NONE)
        ScrugeVoteSquishable(gen).squish(args, byteWriter)
        val bytes = CompressionFactory(CompressionType.NONE).create().compress(byteWriter.toBytes())
        return hexWriter.bytesToHex(bytes, 0, bytes.size, null)
    }

    class ScrugeVoteSquishable(private val abiBinaryGen: AbiBinaryGenTransactionWriter)
        : Squishable<ScrugeVote> {

        override fun squish(obj: ScrugeVote, writer: ByteWriter) {
            writer.putAccountName(obj.eosAccount)
            writer.putInt(obj.userId)
            writer.putInt(obj.campaignId)
            writer.putInt(if (obj.vote) 1 else 0)
        }
    }
}
