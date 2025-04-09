package com.taskease.yksfoundation.Model.ResponseModel

data class CreatePostResponseModel(
    val CONTENT: CreatePostResponse,
    val MSG: String,
    val STS: String
)

data class CreatePostResponse(
    val content: String,
    val createdDate: List<Int>,
    val enabled: Boolean,
    val id: Int,
    val imageUrls: List<String>,
    val likeCount: Int,
    val likedBy: List<Any>,
    val postImage: String,
    val superAdmin: Boolean,
    val title: String,
    val updatedDate: String,
    val user: User
)

data class User(
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