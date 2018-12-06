package com.scruge.scruge.services.api.model

import com.scruge.scruge.model.entity.Comment
import com.scruge.scruge.viewmodel.comment.CommentQuery

// response

data class CommentListResponse(val comments:List<Comment>)

// request

data class CommentRequest(val comment:String, val token:String)

data class CommentListRequest(val q: CommentQuery?) {

    val page = q?.page ?: 0
}