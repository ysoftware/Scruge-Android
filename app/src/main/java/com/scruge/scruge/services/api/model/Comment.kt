package com.scruge.scruge.services.api.model

import com.scruge.scruge.dependencies.serialization.Codable
import com.scruge.scruge.model.entity.Comment
import com.scruge.scruge.viewmodel.comment.CommentQuery
import com.scruge.scruge.viewmodel.comment.CommentSource

// response

data class CommentListResponse(val comments:List<Comment>)

// request

data class CommentLikeRequest(val token:String, val value:Boolean)

class CommentRequest(source: CommentSource, val token:String, val text:String): Codable {

    val parentCommentId = if (source == CommentSource.comment) source.commentObject!!.id else null

    val campaignId:Int? = if (source == CommentSource.campaign) source.campaignObject!!.id else null

    val updateId:String? = if (source == CommentSource.update) source.updateObject!!.id else null
}

class CommentListRequest(q: CommentQuery?, val token:String?):Codable {

    val parentCommentId = if (q?.source == CommentSource.comment) q.source.commentObject!!.id else null

    val campaignId:Int? = if (q?.source == CommentSource.campaign) q.source.campaignObject!!.id else null

    val updateId:String? = if (q?.source == CommentSource.update) q.source.updateObject!!.id else null

    val page = q?.page
}