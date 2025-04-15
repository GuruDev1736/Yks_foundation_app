package com.taskease.yksfoundation.Retrofit


import com.taskease.yksfoundation.Model.RequestModel.AddSocietyRequestModel
import com.taskease.yksfoundation.Model.RequestModel.CreatePostRequestModel
import com.taskease.yksfoundation.Model.RequestModel.CreateUserBySuperAdminRequestModel
import com.taskease.yksfoundation.Model.RequestModel.LoginRequestModel
import com.taskease.yksfoundation.Model.RequestModel.UserRegisterRequestModel
import com.taskease.yksfoundation.Model.ResponseModel.AddSocietyResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.CreatePostResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetAllPostResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetAllSocietyResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetAllUserDisabledResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetCommentByPostResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetUserByPostResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.GetUserBySocietyResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.LoginResponseModel
import com.taskease.yksfoundation.Model.ResponseModel.UserRegisterResponseModel
import com.taskease.yksfoundation.Model.UniversalModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    @POST("society/create")
    fun addSociety(@Body model : AddSocietyRequestModel) : Call<AddSocietyResponseModel>

    @GET("user/society/{id}")
    fun getAllUser(@Path("id") id : Int) : Call<GetUserBySocietyResponseModel>

    @POST("auth/user/register/society/{id}")
    fun registerNewUser(@Path("id") id : Int , @Body model : CreateUserBySuperAdminRequestModel) : Call<UserRegisterResponseModel>

    @PUT("super/changeRole/{userId}/{roleName}")
    fun changeRole(@Path("userId") userId: Int, @Path("roleName") roleName: String): Call<UniversalModel>

    @POST("auth/admin/register/society/{societyId}")
    fun addAdmin(@Path("societyId") societyId: Int, @Body model: CreateUserBySuperAdminRequestModel): Call<UserRegisterResponseModel>

    @DELETE("user/delete/{id}")
    fun deleteUser(@Path("id") id: Int): Call<UniversalModel>

    @POST("super/post/create/{userId}")
    fun createSuperAdminPost(@Path("userId") userId: Int, @Body model: CreatePostRequestModel): Call<CreatePostResponseModel>

    @GET("user/export/{societyId}")
    fun exportUserData(@Path("societyId") societyId: Int): Call<UniversalModel>

    @GET("post/")
    fun getAllPost() : Call<GetAllPostResponseModel>

    @POST("likes/like/{userId}/{postId}")
    fun likePost(@Path("userId") userId: Int, @Path("postId") postId: Int): Call<UniversalModel>

    @GET("likes/users/{postId}")
    fun getLikedUsers(@Path("postId") postId: Int?): Call<GetUserByPostResponseModel>

    @GET("comment/{postId}")
    fun getComments(@Path("postId") postId: Int?): Call<GetCommentByPostResponseModel>

    @GET("admin/disable/user/{societyId}")
    fun disableUser(@Path("societyId") societyId: Int): Call<GetAllUserDisabledResponseModel>

    @PUT("admin/enable/user/{userId}")
    fun enableUser(@Path("userId") userId: Int): Call<UniversalModel>

    @PUT("admin/disable/user/{userId}")
    fun rejectUser(@Path("userId") userId: Int): Call<UniversalModel>

    @GET("admin/disable/post/{societyId}")
    fun getAllDisabledPost(@Path("societyId") societyId: Int): Call<GetAllPostResponseModel>

    @PUT("admin/enable/post/{postId}")
    fun enablePost(@Path("postId") postId: Int): Call<UniversalModel>

    @PUT("admin/disable/post/{postId}")
    fun rejectPost(@Path("postId") postId: Int): Call<UniversalModel>

    @POST("post/create/{userId}/{societyId}")
    fun createPost(@Path("userId") userId: Int, @Path("societyId") societyId: Int, @Body model: CreatePostRequestModel): Call<CreatePostResponseModel>

}

