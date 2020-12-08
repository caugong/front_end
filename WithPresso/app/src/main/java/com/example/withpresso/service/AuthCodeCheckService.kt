package com.example.withpresso.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthCodeCheckService {
    @FormUrlEncoded
    @POST("/customer/auth_code/")
    fun requestAuthCodeCheck (
        @Field("cafe_asin") cafe_asin: Int,
        @Field("phone_num") phone_num: String,
        @Field("auth_code") auth_code: String
    ): Call<Int>
}