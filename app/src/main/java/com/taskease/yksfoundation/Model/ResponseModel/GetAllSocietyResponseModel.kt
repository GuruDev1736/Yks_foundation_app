package com.taskease.yksfoundation.Model.ResponseModel

data class GetAllSocietyResponseModel(
    val CONTENT: List<Society>,
    val MSG: String,
    val STS: String
)

data class Society(
    val address: String,
    val created: Long,
    val id: Int,
    val name: String,
    val owner: String,
    val phone: String
)