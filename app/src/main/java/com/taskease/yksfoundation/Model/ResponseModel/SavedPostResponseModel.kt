package com.taskease.yksfoundation.Model.ResponseModel

data class SavedPostResponseModel(
    val CONTENT: List<CONTENT>,
    val MSG: String,
    val STS: String
)

data class CONTENT(
    val id: Int,
    val post: GetAllPost
)