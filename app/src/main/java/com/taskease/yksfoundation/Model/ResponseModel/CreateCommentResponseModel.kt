package com.taskease.yksfoundation.Model.ResponseModel

data class CreateCommentResponseModel(
    val CONTENT: CreateComment,
    val MSG: String,
    val STS: String
)

data class CreateComment(
    val createdDate: Long,
    val id: Int,
    val text: String,
    val user: User
)