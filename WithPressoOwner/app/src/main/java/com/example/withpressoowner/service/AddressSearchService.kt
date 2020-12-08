package com.example.withpressoowner.service


import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.Serializable

interface AddressService {
    @GET("/addrlink/addrLinkApi.do")
    fun requestAddress(
        @Query("confmKey") confmKey: String,
        @Query("currentPage") currentPage: Int,
        @Query("countPerPage") countPerPage: Int,
        @Query("keyword") keyword: String,
        @Query("resultType") resultType: String
    ): Call<AddressSearchResult>
}