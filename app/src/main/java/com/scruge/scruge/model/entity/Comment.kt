package com.scruge.scruge.model.entity

data class Comment(val id: String, val text: String, val timestamp: Int, val authorName: String?,
                   val authorAvatar: String?)