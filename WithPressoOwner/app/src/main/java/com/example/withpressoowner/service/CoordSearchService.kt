package com.example.practice.service

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CoordSearchService {
    @GET("addrlink/addrCoordApi.do")
    fun requestCoord(
        @Query("confmKey") confmKey: String,
        @Query("admCd") admCd: String,
        @Query("rnMgtSn") rnMgtSn: String,
        @Query("udrtYn") udrtYn: String,
        @Query("buldMnnm") buldMnnm: Int,
        @Query("buldSlno") buldSlno: Int,
        @Query("resultType") resultType: String
    ): Call<CoordSearchResponse>
}