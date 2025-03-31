package com.taskease.yksfoundation.Model.ResponseModel

data class LoginResponseModel(
    val CONTENT: LoginResponse,
    val MSG: String,
    val STS: String
)

data class LoginResponse(
    val bannerUrl: String,
    val enabled: Boolean,
    val fullName: String,
    val societyId: Int,
    val token: String,
    val userId: Int,
    val userName: String,
    val userProfilePic: String,
    val userRole: String
)