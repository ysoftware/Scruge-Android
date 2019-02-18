package com.scruge.scruge.services.api.model

// request

data class AccountRequest(val name:String, val publicKey:String)

// response

data class BoolResponse(val value:Boolean)

data class ResultResponse(val result:Int)

//data class CategoriesResponse(val result: Int, val data:List<Category>)

data class GeneralInfoResponse(val lastSupportedVersion:Int?, val apiVersion:Int)

data class TagsResponse(val tags:List<String>)

data class HTMLResponse(val content:String)