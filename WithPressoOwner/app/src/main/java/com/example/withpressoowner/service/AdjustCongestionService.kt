package com.example.withpressoowner.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AdjustCongestionService {
    @FormUrlEncoded
    @POST("/owner/num_of_customer/")
    fun requestAdjustCongestion (
        @Field("cafe_asin") cafe_asin: Int,
        @Field("num_of_customer") num_of_customer: Int,
        @Field("level") level: Int
    ): Call<Int>
}