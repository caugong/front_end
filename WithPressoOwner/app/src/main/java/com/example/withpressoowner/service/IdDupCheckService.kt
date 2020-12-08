package com.example.withpressoowner.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface IdDupConfirmService{
    @FormUrlEncoded
    @POST("/owner/id_dup_check_service/")
    fun requestIdDupConfirm(
        @Field("owner_id") owner_id:String
    ):Call<String>
}