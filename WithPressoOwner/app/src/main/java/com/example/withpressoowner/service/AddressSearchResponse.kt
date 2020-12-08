package com.example.withpressoowner.service

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddressSearchResult (
    @SerializedName("results") var results: Results
)
data class Results (
    @SerializedName("common") var common: Common,
    @SerializedName("juso") var juso: List<Juso>
): Serializable

data class Common (
    @SerializedName("totalCount") var totalCount: String,
    @SerializedName("currentPage") var currentPage: String,
    @SerializedName("countPerPage") var countPerPage: String,
    @SerializedName("errorCode") var errorCode: String,
    @SerializedName("errorMessage") var errorMessage: String
): Serializable

data class Juso (
    @SerializedName("roadAddr") var roadAddr: String,
    @SerializedName("roadAddrPart1") var roadAddrPart1: String,
    @SerializedName("roadAddrPart2") var roadAddrPart2: String,
    @SerializedName("jibunAddr") var jibunAddr: String,
    @SerializedName("engAddr") var engAddr: String,
    @SerializedName("zipNo") var zipNo: String,
    @SerializedName("admCd") var admCd: String,
    @SerializedName("rnMgtSn") var rnMgtSn: String,
    @SerializedName("bdMgtSn") var bdMgtSn: String,
    @SerializedName("detBdNmList") var detBdNmList: String,
    @SerializedName("bdNm") var bdNm: String,
    @SerializedName("bdKdcd") var bdKdcd: String,
    @SerializedName("siNm") var siNm: String,
    @SerializedName("sggNm") var sggNm: String,
    @SerializedName("emdNm") var emdNm: String,
    @SerializedName("liNm") var liNm: String,
    @SerializedName("rn") var rn: String,
    @SerializedName("udrtYn") var udrtYn: String,
    @SerializedName("buldMnnm") var buldMnnm: String,
    @SerializedName("buldSlno") var buldSlno: String,
    @SerializedName("mtYn") var mtYn: String,
    @SerializedName("lnbrMnnm") var lnbrMnnm: String,
    @SerializedName("lnbrSlno") var lnbrSlno: String,
    @SerializedName("emdNo") var emdNo: String
): Serializable