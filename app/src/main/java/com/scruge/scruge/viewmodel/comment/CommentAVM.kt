package com.scruge.scruge.viewmodel.comment

import com.scruge.scruge.model.entity.Comment
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.model.error.NetworkingError
import com.scruge.scruge.model.error.ScrugeError
import com.scruge.scruge.services.Service
import com.ysoftware.mvvm.array.ArrayViewModel

class CommentAVM(): ArrayViewModel<Comment, CommentVM, CommentQuery>() {

    constructor(source:CommentSource): this() {
        query = CommentQuery(source)
    }

    constructor(comments:List<Comment>, source:CommentSource): this() {
        query = CommentQuery(source)
        setData(comments.map { CommentVM(it) })
    }

    override fun fetchData(query: CommentQuery?, block: (Result<Collection<CommentVM>>) -> Unit) {
        if (query != null) {
            Service.api.getComments(query) { result ->
                block(result.map { it.comments.map { CommentVM(it) } })
            }
        }
    }

    // METHODS

    fun postComment(comment:String, completion:(ScrugeError?)->Unit) {
        query?.let {
            Service.api.postComment(comment, it.source) { result ->
                completion(result.exceptionOrNull()?.let { ErrorHandler.error(it) })
            }
        } ?: completion(NetworkingError.unknown)
    }
}