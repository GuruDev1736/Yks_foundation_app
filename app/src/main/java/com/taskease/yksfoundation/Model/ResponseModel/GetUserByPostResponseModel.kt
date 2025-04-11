package com.taskease.yksfoundation.Model.ResponseModel

data class GetUserByPostResponseModel(
    val CONTENT: List<User>,
    val MSG: String,
    val STS: String
)