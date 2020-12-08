package com.example.withpresso.service

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.Serializable

data class Comment (
    @SerializedName("cafe_asin") var cafe_asin: Int,
    @SerializedName("review_asin") var review_asin: Int,
    /* 회원 프로필 사진 그릴 때 필요 */
    @SerializedName("user_asin") var user_asin: Int,
    /* 회원 이름 보여줄 때 필요 */
    @SerializedName("user_name") var user_name: String,
    /* 리뷰 내용 */
    @SerializedName("review") var review: String
): Serializable

interface ReadReviewService {
    @GET("/customer/review/view/")
    suspend fun requestReadReview (
        @Query("cafe_asin") cafe_asin: Int
    ): List<Comment>
}