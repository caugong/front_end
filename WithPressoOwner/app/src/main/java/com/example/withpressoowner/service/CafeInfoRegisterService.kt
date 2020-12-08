package com.example.withpressoowner.service

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.Serializable

data class CafeAsin (
    @SerializedName("cafe_asin") val cafe_asin: Int
): Serializable

interface CafeInfoRegisterService {
    @FormUrlEncoded
    @POST("/owner/cafe_info_register/")
    fun requestCafeInfoRegister(
        @Field("owner_asin") owner_asin: Int,
        /* 카페 기본 정보 */
        @Field("cafe_name") cafe_name: String,
        @Field("cafe_addr") cafe_addr: String,
        @Field("coor_x") coord_x: String,
        @Field("coor_y") coord_y: String,
        @Field("cafe_hour") cafe_hour: String,
        @Field("cafe_tel") cafe_tel: String,
        @Field("cafe_menu") cafe_menu: String,
        /* 카페 세부 정보 */
        /* 1.책상 */
        @Field("table_info") table_info: String,
        @Field("table_size_info") table_size_info: Int,
        /* 2.의자 */
        @Field("chair_back_info") chair_back_info: Int,
        @Field("chair_cushion_info") chair_cushion_info: String,
        /* 3.플러그 */
        @Field("num_plug") num_plug: Int,
        /* 4.음악 */
        @Field("bgm_info") bgm_info: String,
        /* 5.화장실 정보 */
        @Field("toilet_info") toilet_info: Int,
        @Field("toilet_gender_info") toilet_gender_info: Int,
        /* 6.방역 날짜 */
        @Field("sterilization_info") sterilization_info: String,
        /* 7.흡연실 유무 */
        @Field("smoking_room") smoking_room: Int,
        /* 11.할인율 */
        @Field("discount") discount: Int
        ): Call<CafeAsin>
}