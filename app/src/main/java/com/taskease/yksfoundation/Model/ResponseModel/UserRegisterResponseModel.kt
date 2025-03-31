package com.taskease.yksfoundation.Model.ResponseModel

data class UserRegisterResponseModel(
    val CONTENT: UserRegisterResponse,
    val MSG: String,
    val STS: String
)

data class UserRegisterResponse(
    val address: String,
    val anniversary: Long,
    val bannerUrl: String,
    val birthdate: Long,
    val completeProfile: Boolean,
    val designation: String,
    val email: String,
    val enabled: Boolean,
    val facebook: String,
    val fullName: String,
    val gender: String,
    val id: Int,
    val instagram: String,
    val joinDate: Long,
    val linkedin: String,
    val member: Boolean,
    val phoneNo: String,
    val profile_pic: String,
    val snapChat: String,
    val societyId: Int,
    val twitter: String,
    val voter: Boolean,
    val whatsappNo: String
)