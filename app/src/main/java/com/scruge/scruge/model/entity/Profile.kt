package com.scruge.scruge.model.entity

data class Profile(val login: String, val name: String?, val country: String?, val imageUrl: String?,
                   val description: String?):Comparable<Profile> {

    override fun compareTo(other: Profile): Int = compareValuesBy(this, other) { it.login }
}