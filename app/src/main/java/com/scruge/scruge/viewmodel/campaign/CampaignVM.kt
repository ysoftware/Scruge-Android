package com.scruge.scruge.viewmodel.campaign

import android.net.Uri
import com.scruge.scruge.dependencies.dataformatting.format
import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.model.entity.*
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.services.Service
import com.scruge.scruge.model.ViewState
import com.ysoftware.mvvm.single.ViewModel
import java.net.URL

class CampaignVM(model: Campaign?) : ViewModel<Campaign>(model), PartialCampaignViewModel,
        PartialCampaignModelHolder<Campaign> {

    enum class Status(val value:Int) {

        funding(0), milestone(1), activeVote(2), waiting(3), closed(4);

        companion object {
            fun fromValue(v:Int):Status {
                return when (v) {
                    0 -> funding
                    1 -> milestone
                    2 -> activeVote
                    3 -> waiting
                    4 -> closed
                    else -> funding
                }
            }
        }
    }

    private var id:Int

    var isSubscribed:Boolean? = null
        private set(value) {
            field = value
            notifyUpdated()
        }

    var isBacker:Boolean? = null
        private set(value) {
            field = value
            notifyUpdated()
        }


    var canVote:Boolean? = null
        private set(value) {
            field = value
            notifyUpdated()
        }

    var state: ViewState = ViewState.loading
        private set(value) {
            field = value
            notifyUpdated()
        }

    // SETUP

    init {
        id = model?.id ?: 0 // todo this should be fixed
    }

    constructor(id:Int) : this(null) {
        this.id = id
        load()
    }

    private fun resetViewModels() {
        // todo
    }

    // METHODS


    fun loadDescription(completion:((String)->Unit)) {

    }

    fun loadVoteInfo(completion: (VoteInfo?) -> Unit) {

    }
    fun loadVoteResults(completion: (VoteResult?) -> Unit) {

    }

    fun loadAmountContributed(completion: (Double?) -> Unit) {

    }

//    fun contribute(amount:Double, account: AccountVM, passcode: String, completion: (Error?)->Unit) {
//
//    }

//    fun vote(value: Boolean, account: AccountVM, passcode: String, completion: (Error?)->Unit) {
//
//    }

    // ACTIONS

    fun load() {
        state = ViewState.loading
        reloadData()
    }

    fun reloadData() {
        Service.api.getCampaign(id) { result ->
            result.onSuccess { response ->
                model = response.campaign
                reloadSubscribtionStatus()
                reloadCanVote()
                state = ViewState.ready
            }.onFailure { error ->
                model = null
                state = ViewState.error
                state.errorMessage = ErrorHandler.message(error)
            }
        }
    }

    fun toggleSubscribing() {

    }

    fun reloadSubscribtionStatus() {

    }

    fun reloadCanVote() {

    }

    // VIEW MODELS

    // PROPERTIES

    val team:List<Member> get() = model?.team ?: listOf()

    val commentsCount:Int get() = model?.totalCommentsCount ?: 0

    val social:List<Social> get() = model?.social ?: listOf()

    val about:String get() = model?.about ?: ""

    val status:Status get() = model?.status?.let { Status.fromValue(it) } ?: Status.closed

    val videoUrl:Uri? get() = model?.let { Uri.parse(it.videoUrl.replace("controls=0", "")) }

    // partial

    override val description get() = model?.description ?: ""

    override val title get() = model?.title ?: ""

    override val imageUrl: URL? get() {
        val m = model
        if (m?.imageUrl != null) { return URL(m.imageUrl) }
        return null
    }

    override val progress:Double get() {
        val m = model
        if (m != null) { return m.economics.raised / m.economics.softCap }
        return 0.0
    }

    override val progressString
        get() = "${(progress * 100).formatRounding()}% raised"

    override val raisedString: String get() {
        val m = model
        if (m != null) {
            val raised = m.economics.raised.format()
            val total = m.economics.softCap.format()
            return "$raised raised of $total"
        }
        return ""
    }

    override val daysLeft:String get()  {
        val m = model
        if (m != null) {
            val end = m.endTimestamp
            return "$end"
        }
        else {
            return ""
        }
    }
}