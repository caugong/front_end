package com.example.withpressoowner.service


import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SendAuthCodeService {
    @FormUrlEncoded
    @POST("/owner/phone_num/")
    fun requestSendMsg(
        @Field("cafe_asin") cafe_asin: Int,
        @Field("phone_num") phone_num: String
    ): Call<Int>
}