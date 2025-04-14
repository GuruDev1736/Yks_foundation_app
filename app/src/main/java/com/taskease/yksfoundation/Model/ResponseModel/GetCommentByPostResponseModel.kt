package com.taskease.yksfoundation.Model.ResponseModel

data class GetCommentByPostResponseModel(
    val CONTENT: List<CommentPost>,
    val MSG: String,
    val STS: String
)

data class CommentPost(
    val createdDate: Long,
    val id: Int,
    val text: String,
    val user: User
)