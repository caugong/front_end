package com.example.withpresso.service

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SurveyService {
    @FormUrlEncoded
    @POST("/survey/")
    fun requestSurvey(
        @Field("input_email")email: String,
        @Field("survey1")survey1:Int,
        @Field("survey2")survey2:Int,
        @Field("survey3")survey3:Int
    ): Call<String>
}