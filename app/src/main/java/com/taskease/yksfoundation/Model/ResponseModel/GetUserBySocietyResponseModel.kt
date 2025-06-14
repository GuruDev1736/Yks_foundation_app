package com.taskease.yksfoundation.Model.ResponseModel

data class GetUserBySocietyResponseModel(
    val CONTENT: List<GetUserBySociety>,
    val MSG: String,
    val STS: String
)

data class GetUserBySociety(
    val address: String,
    val anniversary: String,
    val bannerUrl: String,
    val birthdate: String,
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