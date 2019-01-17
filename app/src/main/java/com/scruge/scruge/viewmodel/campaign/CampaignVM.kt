package com.scruge.scruge.viewmodel.campaign

import android.net.Uri
import com.scruge.scruge.dependencies.dataformatting.dateToRelative
import com.scruge.scruge.model.entity.*
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.services.Service
import com.scruge.scruge.model.ViewState
import com.scruge.scruge.model.error.ScrugeError
import com.scruge.scruge.model.error.WalletError
import com.scruge.scruge.services.eos.ScrugeVote
import com.scruge.scruge.services.eos.Token
import com.scruge.scruge.services.eos.toEosName
import com.scruge.scruge.viewmodel.account.AccountVM
import com.scruge.scruge.viewmodel.comment.CommentAVM
import com.scruge.scruge.viewmodel.comment.CommentSource
import com.scruge.scruge.viewmodel.document.DocumentAVM
import com.scruge.scruge.viewmodel.economies.EconomiesVM
import com.scruge.scruge.viewmodel.faq.FaqAVM
import com.scruge.scruge.viewmodel.milestone.MilestoneAVM
import com.scruge.scruge.viewmodel.milestone.MilestoneVM
import com.scruge.scruge.viewmodel.update.UpdateVM
import com.ysoftware.mvvm.single.ViewModel
import kotlin.math.roundToInt

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

    private var id:Int? = null

    var isSubscribed:Boolean = false
        private set(value) {
            field = value
            notifyUpdated()
        }

    var isBacker:Boolean = false
        private set(value) {
            field = value
            notifyUpdated()
        }


    var canVote:Boolean = false
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
        id = model?.id
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
        val model = model ?: return completion("")
        Service.api.getCampaignContent(model) { result ->
            completion(result.getOrNull()?.content ?: "")
        }
    }

    fun loadVoteInfo(completion: (VoteInfo?) -> Unit) {
        val model = model ?: return completion(null)
        Service.api.getVoteInfo(model.id) { result ->
            completion(result.getOrNull()?.voting)
        }
    }

    fun loadVoteResults(completion: (VoteResult?) -> Unit) {
        val model = model ?: return completion(null)
        Service.api.getVoteResult(model.id) { result ->
            completion(result.getOrNull()?.votings?.firstOrNull { it.active })
        }
    }

    fun loadAmountContributed(completion: (Double?) -> Unit) {
        val model = model ?: return completion(null)
        Service.api.getContributionHistory { result ->
            val contrib = result.getOrNull()?.contributions?.firstOrNull { it.campaignId == model.id }
            completion(contrib?.amount)
        }
    }

    fun contribute(amount:Double, account: AccountVM, passcode: String, completion: (ScrugeError?)->Unit) {
        val model = this.model ?: return completion(WalletError.noAccounts)
        val account = account.model ?: return completion(WalletError.noAccounts)

        Service.api.getUserId { userResult ->
            userResult.onSuccess {
                Service.eos.sendMoney(account,
                                      Service.eos.contractAccount,
                                      amount,
                                      Token.Scruge,
                                      "${it.userId}-${model.id}",
                                      passcode) { transactionResult ->
                    transactionResult.onSuccess { transactionId ->
                        Service.api.notifyContribution(model.id, amount, transactionId) {
                            completion(null)
                        }
                    }.onFailure {
                        completion(ErrorHandler.error(it))
                    }
                }
            }.onFailure {
                completion(ErrorHandler.error(it))
            }
        }
    }

    fun vote(value: Boolean, account: AccountVM, passcode: String, completion: (ScrugeError?)->Unit) {
        val model = this.model ?: return completion(WalletError.noAccounts)
        val account = account.model ?: return completion(WalletError.noAccounts)

        Service.api.getUserId { userResult ->
            userResult.onSuccess {
                val data = ScrugeVote(account.name,
                                      it.userId,
                                      model.id,
                                      value)

                Service.eos.sendAction("vote".toEosName()!!,
                                       account = account,
                                       data = data,
                                       passcode = passcode) { transactionResult ->
                    transactionResult.onSuccess { transactionId ->
                        Service.api.notifyVote(model.id, value, transactionId) {
                            completion(null)
                        }
                    }.onFailure {
                        completion(ErrorHandler.error(it))
                    }
                }
            }.onFailure {
                completion(ErrorHandler.error(it))
            }
        }
    }

    // ACTIONS

    fun load() {
        state = ViewState.loading
        reloadData()
    }

    fun reloadData() {
        val id = this.id ?: return
        Service.api.getCampaign(id) { result ->
            isBacker = false
            isSubscribed = false
            canVote = false

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
            resetViewModels()
        }
    }

    fun toggleSubscribing() {
        if (model == null) { return }

        val newValue = !isSubscribed
        Service.api.setSubscribing(newValue, model!!) { result ->
            if (result.getOrNull()?.result == 0) {
                reloadSubscribtionStatus()
            }
        }
    }

    fun reloadSubscribtionStatus() {
        if (model == null) { return }

        Service.api.getSubscriptionStatus(model!!) { result ->
            result.onSuccess {
                isSubscribed = it.value
            }.onFailure {
                isSubscribed = false
            }
        }
    }

    fun reloadCanVote() {
        if (model == null) { return }

        Service.api.getDidContribute(model!!.id) { didContributeResult ->
            didContributeResult.onSuccess {
                isBacker = it.value

                if (!it.value) {
                    canVote = false
                    return@onSuccess
                }

                Service.api.getDidVote(model!!.id) { didVoteResult ->
                    didVoteResult.onSuccess {
                        canVote = !it.value // if did vote, then can't vote now
                    }.onFailure {
                        canVote = false
                    }
                }
            }.onFailure {
                canVote = false
                isBacker = false
            }
        }
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

    override val imageUrl: Uri? get() {
        val m = model
        if (m?.imageUrl != null) { return try { Uri.parse(m.imageUrl) } catch (e:Exception) { null }}
        return null
    }

    override val raised get() = model?.economics?.raised?.roundToInt()?.toDouble() ?: 0.0

    override val hardCap get() = model?.economics?.hardCap ?: 0

    override val softCap get() = model?.economics?.softCap ?: 0

    override val daysLeft:String get() = model?.let { dateToRelative(it.endTimestamp, "ends", "ended") } ?: ""
}