package com.example.withpresso.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ReviewWriteService {
    @FormUrlEncoded
    @POST("/customer/review/write/")
    fun requestWriteReview (
        @Field("user_asin") user_asin: Int,
        @Field("cafe_asin") cafe_asin: Int,
        @Field("review_contents") review_content: String
    ): Call<Int>
}