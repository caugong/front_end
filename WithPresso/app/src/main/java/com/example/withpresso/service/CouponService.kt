package com.example.withpresso.service

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.Serializable

data class Coupon (
    // 1. 카페 고유 번호
    @SerializedName("cafe_asin") var cafe_asin: Int,
    // 2. 쿠폰 고유 번호
    @SerializedName("coupon_asin") var coupon_asin: String,
    // 3. 할인율
    @SerializedName("discount_rate") var discount: String,
    // 4. 카페 이름
    @SerializedName("cafe_name") var cafe_name: String,
    // 5. 쿠폰 종류 (리뷰 또는 3시간)
    @SerializedName("coupon_type") var coupon_type: String,
    // 6. 유효기간
    @SerializedName("validity") var validity: String,
    // 7. 쿠폰 코드
    @SerializedName("coupon_code") var coupon_code: String,
    // 8. 유저 고유 번호
    @SerializedName("user_asin") var user_asin: Int
): Serializable


interface CouponService {
    @GET("/customer/coupon/check")
    fun requestCoupon (
        @Query("user_asin") user_asin: Int
    ): Call<List<Coupon>>
}