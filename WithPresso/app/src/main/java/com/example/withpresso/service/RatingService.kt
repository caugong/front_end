package com.example.withpresso.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RatingService {
    @FormUrlEncoded
    @POST("/customer/rating")
    fun requestReflectRating (
        @Field("user_clean_info") user_clean_info: Int,
        @Field("user_toilet_clean_info") user_toilet_clean_info: Int,
        @Field("user_noisy_info") user_noisy_info: Int,
        @Field("user_good_study_info") user_good_study_info: Int,
        @Field("cafe_asin") cafe_asin: Int,
        @Field("user_asin") user_asin: Int
    ): Call<Int>
}