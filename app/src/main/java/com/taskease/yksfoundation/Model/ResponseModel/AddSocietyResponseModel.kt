package com.taskease.yksfoundation.Model.ResponseModel

data class AddSocietyResponseModel(
    val CONTENT: AddSociety,
    val MSG: String,
    val STS: String
)

data class AddSociety(
    val address: String,
    val created: Long,
    val id: Int,
    val name: String,
    val owner: String,
    val phone: String
)