package com.taskease.yksfoundation.Model.RequestModel

data class UserRegisterRequestModel(
    val address: String,
    val anniversary: String,
    val birthdate: String,
    val designation: String,
    val email: String,
    val facebook: String,
    val fullName: String,
    val gender: String,
    val instagram: String,
    val linkedin: String,
    val member: Boolean,
    val password: String,
    val phoneNo: String,
    val profile_pic: String,
    val snapChat: String,
    val twitter: String,
    val voter: Boolean,
    val whatsappNo: String
)