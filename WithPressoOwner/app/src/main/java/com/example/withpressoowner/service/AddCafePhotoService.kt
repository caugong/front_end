package com.example.withpressoowner.service

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AddCafePhotoService {
    @Multipart
    @POST("/owner/cafe_photo")
    fun requestAddPhoto(
        @Part("cafe_asin") cafe_asin: Int,
        @Part("num_of_pics") num_of_pics: Int,
        @Part cafe_photo: ArrayList<MultipartBody.Part>
    ): Call<Int>
}