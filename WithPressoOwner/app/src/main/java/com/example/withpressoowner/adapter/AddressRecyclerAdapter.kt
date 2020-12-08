package com.example.withpressoowner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.practice.service.CoordSearchResponse
import com.example.practice.service.CoordSearchService
import com.example.withpressoowner.R
import com.example.withpressoowner.R.layout.fragment_address_search
import com.example.withpressoowner.R.layout.fragment_basic_info
import com.example.withpressoowner.fragment.AddressSearchFragment
import com.example.withpressoowner.service.Juso
import kotlinx.android.synthetic.main.address_text.view.*
import kotlinx.android.synthetic.main.fragment_basic_info.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class AddressViewHolder (item: View):RecyclerView.ViewHolder(item) {
    var addr = item.address_text
}

class AddressRecyclerAdapter (
    val context: Context, val jusoList: MutableList<Juso>): RecyclerView.Adapter<AddressViewHolder>() {
    private val coordConfmKey = "devU01TX0FVVEgyMDIwMTEyMzIxMjAyMzExMDQ1Njk="

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val addr = LayoutInflater.from(context).inflate(R.layout.address_text, parent, false)
        return AddressViewHolder(addr)
    }

    override fun getItemCount(): Int = jusoList.size

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val currJuso = jusoList[position]
        holder.addr.text = currJuso.roadAddr

        holder.itemView.setOnClickListener {
            val coordSearchService = Retrofit.Builder()
                .baseUrl("http://www.juso.go.kr")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CoordSearchService::class.java)
            coordSearchService.requestCoord(
                coordConfmKey,
                currJuso.admCd,
                currJuso.rnMgtSn,
                currJuso.udrtYn,
                currJuso.buldMnnm.toInt(),
                currJuso.buldSlno.toInt(),
                "json"
            ).enqueue(object :Callback<CoordSearchResponse> {
                override fun onFailure(call: Call<CoordSearchResponse>, t: Throwable) {
                    Log.e("coord search request", "failed")
                }

                @SuppressLint("CommitPrefEdits", "ApplySharedPref")
                override fun onResponse(
                    call: Call<CoordSearchResponse>,
                    response: Response<CoordSearchResponse>
                ) {
                    val coordSearchResult = response.body()
                    coordSearchResult?.let {
                        val info_pref = context.getSharedPreferences("cafe_info", 0)
                        info_pref.edit().apply {
                            putString("cafe_addr", currJuso.roadAddr)
                            putString("coord_x", coordSearchResult.results.juso[0].entX)
                            putString("coord_y", coordSearchResult.results.juso[0].entY)
                        }.commit()
                        Log.d("coord X", coordSearchResult.results.juso[0].entX)
                        Log.d("coord Y", coordSearchResult.results.juso[0].entY)
                        findNavController(FragmentManager.findFragment(holder.itemView)).navigate(
                            R.id.action_addressSearchFragment_to_basicInfoFragment
                        )
                    }
                }
            })

        }
    }
}