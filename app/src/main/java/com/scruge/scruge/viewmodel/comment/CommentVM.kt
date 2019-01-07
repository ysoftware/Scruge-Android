package com.scruge.scruge.viewmodel.comment

import android.net.Uri
import com.scruge.scruge.dependencies.dataformatting.datePresent
import com.scruge.scruge.model.entity.Comment
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.services.Service
import com.ysoftware.mvvm.single.ViewModel
import java.lang.Exception

class CommentVM(model: Comment?) : ViewModel<Comment>(model) {

    val authorName get() = model?.authorName ?: "Anonymous"

    val authorPhoto:Uri? get() = model?.authorAvatar?.let { try { Uri.parse(it) } catch(e:Exception) { null }}

    val comment get() = model?.text ?: ""

    val date get() = model?.let { datePresent(it.timestamp, "d MMMM, H:mm") }

    val likes get() = model?.let { if (it.likeCount != 0) it.likeCount.toString() else "" } ?: ""

    val canReply get() = model?.repliesCount != null

    val isLiking get() = model?.isLiking == true

    val repliesText:String? get() {
        return model?.repliesCount?.let { number ->
            if (number == 0) { return "" }
            val replies = if (number == 1) "reply" else "replies"
            return "See $number $replies"
        }
    }

    fun like() {
        model?.let { model ->
            val newValue = !isLiking
            Service.api.likeComment(model, newValue) { result ->
                result.onSuccess { response ->
                    if (ErrorHandler.error(response.result) == null) {
                        this.model?.let {
                            it.likeCount += if (newValue) 1 else -1
                            it.isLiking = newValue
                            notifyUpdated()
                        }
                    }
                }
            }
        }
    }
}