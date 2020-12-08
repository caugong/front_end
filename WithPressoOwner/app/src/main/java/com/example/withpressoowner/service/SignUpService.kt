package com.example.withpressoowner.service

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.Serializable

data class OwnerAsin(
    @SerializedName("owner_asin") val owner_asin: Int
): Serializable

interface SignUpService {
    @FormUrlEncoded
    @POST("/owner/sign_up_service/")
    suspend fun requestSignUp(
        @Field("owner_id") owner_id: String,
        @Field("owner_pw") owner_pw: String,
        @Field("owner_name") owner_name: String,
        @Field("busi_num") busi_num: String
    ): OwnerAsin
}