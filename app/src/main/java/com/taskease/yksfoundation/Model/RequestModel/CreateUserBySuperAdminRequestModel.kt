package com.taskease.yksfoundation.Model.RequestModel

data class CreateUserBySuperAdminRequestModel(
    val address: String,
    val email: String,
    val enabled: Boolean,
    val fullName: String,
    val gender: String,
    val password: String,
    val phoneNo: String,
    val profile_pic: String
)