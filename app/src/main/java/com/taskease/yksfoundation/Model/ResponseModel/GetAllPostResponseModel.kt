package com.taskease.yksfoundation.Model.ResponseModel

data class GetAllPostResponseModel(
    val CONTENT: List<GetAllPost>,
    val MSG: String,
    val STS: String
)

data class GetAllPost(
    val content: String,
    val createdDate: List<Int>,
    val enabled: Boolean,
    val id: Int,
    val imageUrls: List<String>,
    val likeCount: Int,
    val likedBy: List<Int>,
    val savedBy: List<Int>,
    val postImage: String,
    val superAdmin: Boolean,
    val title: String,
    val updatedDate: Any,
    val user: User
)