package com.taskease.yksfoundation.Retrofit


import com.taskease.yksfoundation.Model.RequestModel.UserRegisterRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.GetAllSocietyResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.UserRegisterResponseModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {

    @GET("society/all")
    fun getAllSociety() : Call<GetAllSocietyResponseModel>

    @POST("auth/user/register/society/{id}")
    fun registerUser(@Path("id") id : Int , @Body model : UserRegisterRequestModel) : Call<UserRegisterResponseModel>

}