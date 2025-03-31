package com.taskease.yksfoundation.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.taskease.yksfoundation.Model.RegisterModel
import java.util.Date

class RegisterViewModel : ViewModel() {

    private val _signupData = MutableLiveData<RegisterModel>()
    val signupData: LiveData<RegisterModel> get() = _signupData

    init {
        _signupData.value = RegisterModel(
            fullName = "",
            email = "",
            password = "",
            phoneNo = "",
            profilePic = "",
            designation = "",
            address = "",
            gender = "",
            facebookLink = "",
            twitterLink = "",
            instagramLink = "",
            linkedinLink ="",
            snapchatLink ="",
            whatsappNo = "",
            voter = false,
            member = false,
            dateOfBirth = "",
            anniversaryDate = "",
            societyId = 0
        )
    }

    fun selectSociety(societyId : Int)
    {
        _signupData.value?.apply {
            this.societyId = societyId
        }
    }

    fun personalDetails(fullName : String,email : String,password : String , gender : String , designation : String , aniversaryDate : String , birthDate: String , phoneNo : String , whatsappNo : String , voter : Boolean , member : Boolean , address : String)
    {
        _signupData.value?.apply {
            this.fullName = fullName
            this.email = email
            this.password = password
            this.gender = gender
            this.designation = designation
            this.anniversaryDate = aniversaryDate
            this.dateOfBirth = birthDate
            this.phoneNo = phoneNo
            this.whatsappNo = whatsappNo
            this.voter = voter
            this.member = member
            this.address = address
        }
    }

    fun socialDetails(facebookLink : String,twitterLink : String,instagramLink : String,linkedinLink : String,snapchatLink : String){
        _signupData.value?.apply {
            this.facebookLink = facebookLink
            this.twitterLink = twitterLink
            this.instagramLink = instagramLink
            this.linkedinLink = linkedinLink
            this.snapchatLink = snapchatLink
        }
    }

    fun profilePic(profilePic : String){
        _signupData.value?.apply {
            this.profilePic = profilePic
        }
    }
}