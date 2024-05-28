package com.example.freeimagefeed.domain.models

data class UserDetailModel(
    val id: String = "",
    val title: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val picture: String = "",
    val gender: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val phone: String  = "",
    val location: LocationModel = LocationModel("","","","",""),
    val registerDate: String = "",
    val updatedDate: String = "",
    var isFriend: Boolean = false
)

data class LocationModel(
    val street: String = "",
    val city: String = "",
    val state: String   = "",
    val country: String = "",
    val timezone: String    = ""
)