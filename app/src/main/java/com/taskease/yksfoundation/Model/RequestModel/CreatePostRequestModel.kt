package com.taskease.yksfoundation.Model.RequestModel

data class CreatePostRequestModel(
    val content: String,
    val postImage: String,
    val title: String
)