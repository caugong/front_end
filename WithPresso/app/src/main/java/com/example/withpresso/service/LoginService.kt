package com.example.withpresso.service

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.Serializable

data class Login (
    @SerializedName("user_asin") var uniq_num: Int,
    @SerializedName("user_name") var nickname: String,
    @SerializedName("image_location") var profile: String
):Serializable

/* 로그인에 성공하면 사진 url 받기 */
interface LoginService {
    @FormUrlEncoded
    @POST("/login/")
    fun requestLogin(
        @Field("user_id")id: String,
        @Field("user_pw")pw: String
    ): Call<Login>
}
