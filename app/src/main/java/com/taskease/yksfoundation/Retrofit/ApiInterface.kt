package com.taskease.yksfoundation.Retrofit


import com.taskease.yksfoundation.Model.RequestModel.LoginRequestModel
import com.taskease.yksfoundation.Model.RequestModel.UserRegisterRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.GetAllSocietyResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.LoginResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.UserRegisterResponseModel
import com.taskease.yksfoundation.Model.UniversalModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("society/all")
    fun getAllSociety() : Call<GetAllSocietyResponseModel>

    @POST("auth/user/register/society/{id}")
    fun registerUser(@Path("id") id : Int , @Body model : UserRegisterRequestModel) : Call<UserRegisterResponseModel>

    @POST("auth/login")
    fun login(@Body model : LoginRequestModel) : Call<LoginResponseModel>

    @POST("auth/send-otp")
    fun sendOtp(@Query("email") email : String) : Call<UniversalModel>

    @POST("auth/validate-otp")
    fun validateOtp(@Query("email") email : String , @Query("otp") otp : String) : Call<UniversalModel>

    @PUT("auth/changePassword")
    fun changePassword(@Query("email") email : String , @Query("password") password : String) : Call<UniversalModel>
}