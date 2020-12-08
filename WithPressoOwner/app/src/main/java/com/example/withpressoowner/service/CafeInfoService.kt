package com.example.withpressoowner.service

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.Serializable

data class CafeInfo (
    /* basic */
    @SerializedName("cafe_name") var cafe_name: String,
    @SerializedName("cafe_addr") var cafe_addr: String,
    @SerializedName("cafe_hour") var cafe_hour: String,
    @SerializedName("cafe_tel") var cafe_tel:String,
    @SerializedName("cafe_menu") var cafe_menu:String,
    /* photo */
    @SerializedName("num_of_pics") var num_of_pics: Int,
    /* congestion */
    @SerializedName("num_of_customer") var num_of_customer: Int,
    @SerializedName("complexity_level") var level: Int,
    /* table*/
    @SerializedName("table_info") var table_struct: String,
    @SerializedName("table_size_info") var table_size: Int,
    /* chair */
    @SerializedName("chair_back_info") var chair_back: Int,
    @SerializedName("chair_cushion_info") var chair_cushion: String,
    /* # of plug */
    @SerializedName("num_plug") var num_plug: Int,
    /* music*/
    @SerializedName("bgm_info") var music_genre: String,
    /* restroom */
    @SerializedName("toilet_info") var rest_in: Int,
    @SerializedName("toilet_gender_info") var rest_gen_sep: Int,
    /*anti-corona */
    @SerializedName("sterilization_info") var anco_data: String,
    /*smoking room*/
    @SerializedName("smoking_room") var smoking_room: Int,
    /*discount*/
    @SerializedName("discount") var discount: Int,
    /* user review */
    @SerializedName("user_clean_info") var cafe_clean: Float,
    @SerializedName("user_toilet_clean_info") var rest_clean: Float,
    @SerializedName("user_noisy_info") var noise: Float,
    @SerializedName("user_good_study_info") var study_well: Float
): Serializable

interface CafeInfoService {
    @GET("/owner/owners_cafe_infomation")
    suspend fun requestCafeInfo(
        @Query("cafe_asin") cafe_asin: Int
    ): CafeInfo
}