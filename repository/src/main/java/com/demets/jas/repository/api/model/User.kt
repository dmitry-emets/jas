package com.demets.jas.repository.api.model

import com.google.gson.annotations.SerializedName

/**
 * Created by dmitry on 26/11/2017.
 */
data class User(
        val name: String,
        val realname: String,
        val image: List<Image>,
        val url: String,
        val country: String,
        val age: Int,
        val gender: String,
        val subscriber: String,
        val playcount: Int,
        val playlists: Int,
        val bootstrap: Int,
        val registered: Time,
        val type: String
)

data class UserListWrapper(
        @SerializedName("user")
        val users: List<User>)

data class UserInfoResponse(
        val user: User
)

data class UserFriendsResponse(
        val friends: UserListWrapper
)
