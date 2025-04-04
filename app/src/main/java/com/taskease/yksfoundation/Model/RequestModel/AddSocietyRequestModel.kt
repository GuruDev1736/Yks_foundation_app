package com.taskease.yksfoundation.Model.RequestModel

data class AddSocietyRequestModel(
    val address: String,
    val name: String,
    val owner: String,
    val phone: String
)