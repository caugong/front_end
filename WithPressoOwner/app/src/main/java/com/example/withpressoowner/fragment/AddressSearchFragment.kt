package com.example.withpressoowner.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.withpressoowner.R
import com.example.withpressoowner.adapter.AddressRecyclerAdapter
import com.example.withpressoowner.service.AddressSearchResult
import com.example.withpressoowner.service.AddressService
import com.example.withpressoowner.service.Juso
import kotlinx.android.synthetic.main.fragment_address_search.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.support.v4.longToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.Delegates

class AddressSearchFragment : Fragment() {
    private lateinit var roadConfmKey: String
    private lateinit var retrofit: Retrofit
    private lateinit var roadAddrList: MutableList<Juso>

    private var beforeAddr: String? = null
    private var sameAddr = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_address_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*init*/
        retrofit = Retrofit.Builder()
            .baseUrl("http://www.juso.go.kr")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        roadConfmKey = "devU01TX0FVVEgyMDIwMTExOTIwNDQ0MTExMDQ0MjA="

        roadAddrList = mutableListOf()

        address_search_recycler.layoutManager = LinearLayoutManager(requireContext())

        address_search_image.setOnClickListener {
            address_search_edit.text?.let {
                val address = address_search_edit.text.toString()

                sameAddr = beforeAddr == address

                val addressService = retrofit.create(AddressService::class.java)
                addressService.requestAddress(
                    roadConfmKey,
                    1,
                    10,
                    address,
                    "json").enqueue(object : Callback<AddressSearchResult> {
                    override fun onFailure(call: Call<AddressSearchResult>, t: Throwable) {
                        t.message?.let { it1 ->
                            longToast(it1)
                            Log.d("fail", it1)
                        }
                    }

                    override fun onResponse(call: Call<AddressSearchResult>, response: Response<AddressSearchResult>) {
                        val addressResult = response.body()
                        addressResult?.let {
                            val result = addressResult.results
                            result?.let {
                                if(!sameAddr)
                                    roadAddrList.clear()

                                for(juso in result.juso) {
                                    roadAddrList.add(juso)
                                }
                                address_search_recycler.adapter = AddressRecyclerAdapter(
                                    requireContext(), roadAddrList
                                )
                            }
                        }
                    }

                })
            }
        }
    }
}