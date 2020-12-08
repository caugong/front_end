package com.example.withpressoowner.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UseCouponService {
    @FormUrlEncoded
    @POST("/owner/coupon/check/")
    fun requestDeleteCoupon (
        @Field("cafe_asin") cafe_asin: Int,
        @Field("coupon_code") coupon_code: String
    ): Call<Int>
}