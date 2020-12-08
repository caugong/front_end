package com.example.withpresso.service

import retrofit2.Call
import retrofit2.http.*


public interface SignUpService{
    @FormUrlEncoded
    @POST("/signup/")
    fun requestSignUp(
        @Field("input_email")email: String,
        @Field("input_pw")password: String,
        @Field("input_nick")nickname: String,
        @Field("input_phone")phone: String
    ): Call<String>
}