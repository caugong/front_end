package com.example.withpresso.service

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File
import java.io.Serializable

interface ProfileReplaceService {
    @Multipart
    @POST("/profile/")
    fun requestProfileReplacement(
        @Part uniq_num: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): Call<String>
}