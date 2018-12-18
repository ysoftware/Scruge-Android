package com.scruge.scruge.viewmodel.comment

import android.net.Uri
import com.scruge.scruge.dependencies.dataformatting.datePresent
import com.scruge.scruge.model.entity.Comment
import com.ysoftware.mvvm.single.ViewModel
import java.lang.Exception

class CommentVM(model: Comment?) : ViewModel<Comment>(model) {

    val authorName get() = model?.authorName ?: "Anonymous"

    val authorPhoto:Uri? get() = model?.authorAvatar?.let { try { Uri.parse(it) } catch(e:Exception) { null }}

    val comment get() = model?.text ?: ""

    val date get() = model?.let { datePresent(it.timestamp, "d MMMM, H:mm") }

    val likes get() = model?.let { it.likeCount?.let { if (it != 0) it.toString() else "" }} ?: ""
}