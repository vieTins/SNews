package com.example.securescan.data.models

data class User(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val city: String = "",
    val profilePic: String  ?= null,
    val fcmToken: String = ""
)
