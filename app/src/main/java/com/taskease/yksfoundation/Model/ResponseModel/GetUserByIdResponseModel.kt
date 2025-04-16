package com.taskease.yksfoundation.Model.ResponseModel

data class GetUserByIdResponseModel(
    val CONTENT: User,
    val MSG: String,
    val STS: String
)