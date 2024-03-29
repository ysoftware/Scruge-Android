package com.scruge.scruge.model.entity

data class Comment(val id: String, val text: String, val timestamp: Long, val authorName: String?,
                   val authorAvatar: String?, var likeCount:Int,
                   var isLiking:Boolean?, val repliesCount:Int?):Comparable<Comment> {

    override fun compareTo(other: Comment): Int = compareValuesBy(this, other) { it.id }
}