package com.taskease.yksfoundation.Model

import java.util.Date

data class RegisterModel(
    var fullName : String? ="",
    var email : String? ="",
    var password : String? ="",
    var phoneNo : String? ="",
    var profilePic : String? ="",
    var designation : String?="",
    var address : String?="",
    var dateOfBirth : String?="",
    var anniversaryDate : String?="",
    var gender : String?="",
    var facebookLink : String?="",
    var twitterLink : String?="",
    var instagramLink : String?="",
    var linkedinLink : String?="",
    var snapchatLink : String?="",
    var whatsappNo : String?="",
    var voter : Boolean,
    var member : Boolean,
    var societyId : Int
)
