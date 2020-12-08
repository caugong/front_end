package com.example.practice.service


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CoordSearchResponse (
    @SerializedName("results") var results: CoordResults
): Serializable

data class CoordResults (
    @SerializedName("common") var common: CoordCommon,
    @SerializedName("juso") var juso: List<CoordJuso>
): Serializable

data class CoordCommon (
    @SerializedName("totalCount") var totalCount: String,
    @SerializedName("errorCode") var errorCode: String,
    @SerializedName("errorMessage") var errorMessage: String
): Serializable

data class CoordJuso (
    @SerializedName("admCd") var admCd: String,
    @SerializedName("rnMgtSn") var rnMgtSn: String,
    @SerializedName("bdMgtSn") var bdMgtSn: String,
    @SerializedName("udrtYn") var udrtYn: String,
    @SerializedName("buldMnnm") var buldMnnm: String,
    @SerializedName("buldSlno") var buldSlno: String,
    @SerializedName("entX") var entX: String,
    @SerializedName("entY") var entY: String,
    @SerializedName("bdNm") var bdNm: String
): Serializable