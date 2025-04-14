package com.taskease.yksfoundation.Model.ResponseModel

data class GetAllUserDisabledResponseModel(
    val CONTENT: List<User>,
    val MSG: String,
    val STS: String
)