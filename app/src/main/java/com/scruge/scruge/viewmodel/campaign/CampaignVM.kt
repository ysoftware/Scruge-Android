package com.scruge.scruge.viewmodel.campaign

import android.net.Uri
import com.scruge.scruge.dependencies.dataformatting.format
import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.model.entity.*
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.services.Service
import com.scruge.scruge.model.ViewState
import com.scruge.scruge.viewmodel.comment.CommentAVM
import com.scruge.scruge.viewmodel.comment.CommentSource
import com.scruge.scruge.viewmodel.document.DocumentAVM
import com.scruge.scruge.viewmodel.economies.EconomiesVM
import com.scruge.scruge.viewmodel.faq.FaqAVM
import com.scruge.scruge.viewmodel.milestone.MilestoneAVM
import com.scruge.scruge.viewmodel.milestone.MilestoneVM
import com.scruge.scruge.viewmodel.update.UpdateVM
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
        lastUpdateVM = model?.lastUpdate?.let { UpdateVM(it) }
        currentMilestoneVM = model?.currentMilestone?.let { MilestoneVM(it) }
        faqVM = model?.faq?.let { FaqAVM(it) }
        economiesVM = model?.economics?.let { EconomiesVM(it) }

        milestonesVM = model?.let { MilestoneAVM(it) }
        milestonesVM?.reloadData()

        topCommentsVM = model?.let { m ->
            val source = CommentSource.campaign
            source.campaignObject = m
            m.topComments?.let { CommentAVM(it, source)}
        }

        documentsVM = model?.let { model ->
            val documents = arrayListOf<Document>()

            if (model.pitchUrl != null) {
                documents.add(Document("Pitch", model.pitchUrl))
            }
            else {
                val url = Service.api.serviceUrl + "campaign/${model.id}/content"
                documents.add(Document("Pitch", url))
            }
            model.documents?.let {
                documents.addAll(it)
            }
            DocumentAVM(documents)
        }
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

    var lastUpdateVM: UpdateVM? = null; private set

    var topCommentsVM:CommentAVM? = null; private set

    var currentMilestoneVM:MilestoneVM? = null; private set

    var milestonesVM:MilestoneAVM? = null; private set

    var economiesVM:EconomiesVM? = null; private set

    var documentsVM:DocumentAVM? = null; private set

    var faqVM: FaqAVM? = null; private set

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