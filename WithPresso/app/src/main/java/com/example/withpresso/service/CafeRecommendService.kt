package com.example.withpresso.service

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.Serializable
import java.util.ArrayList

data class Cafe(
    @SerializedName("cafe_asin") val cafe_uniq_num: Int,
    @SerializedName("cafe_name") var name: String
): Serializable

interface CafeRecommendService {
    @GET("/cafe_recommend/")
    suspend fun requestCafeRecommendList(
        @Query("lat") latitude: Double,
        @Query("long") longitude: Double,
        @Query("uniq_num") uniq_num: Int,
        @Query("page_num") page_num: Int
    ): ArrayList<Cafe>
}