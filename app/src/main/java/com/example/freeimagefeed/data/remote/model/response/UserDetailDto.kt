package com.example.freeimagefeed.data.remote.model.response

import com.example.freeimagefeed.domain.models.LocationModel
import com.example.freeimagefeed.domain.models.UserDetailModel

data class UserDetailDto(
    val id: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val picture: String,
    val gender: String,
    val email: String,
    val dateOfBirth: String,
    val phone: String,
    val location: LocationDto,
    val registerDate: String,
    val updatedDate: String
){
    fun toDomainModel(): UserDetailModel {
        return UserDetailModel(
            id = this.id,
            title = this.title,
            firstName = this.firstName,
            lastName = this.lastName,
            picture = this.picture,
            gender = this.gender,
            email = this.email,
            dateOfBirth = this.dateOfBirth,
            phone = this.phone,
            location = this.location.toDomainModel(),
            registerDate = this.registerDate,
            updatedDate = this.updatedDate,
        )
    }
}

data class LocationDto(
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val timezone: String
){
    fun toDomainModel(): LocationModel {
        return LocationModel(
            street = this.street,
            city = this.city,
            state = this.state,
            country = this.country,
            timezone = this.timezone
        )
    }
}
