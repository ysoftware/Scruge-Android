package com.scruge.scruge.model.entity

data class Member(val name: String, val description: String, val position: String, val imageUrl: String,
                  val social: List<Social>)