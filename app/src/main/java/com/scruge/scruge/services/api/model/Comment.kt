package com.scruge.scruge.services.api.model

import com.scruge.scruge.model.entity.Comment

// response

data class CommentListResponse(val result:Int, val comments:List<Comment>)

// request

data class CommentRequest(val comment:String, val token:String)

data class CommentListRequest(q:CommentQuery?) {

    val page = q.page ?: 0
}