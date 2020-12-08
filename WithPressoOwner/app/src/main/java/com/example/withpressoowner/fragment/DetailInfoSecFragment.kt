package com.example.withpressoowner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import com.example.withpressoowner.R
import com.example.withpressoowner.service.CafeAsin
import com.example.withpressoowner.service.CafeInfoRegisterService
import kotlinx.android.synthetic.main.fragment_detail_info_sec.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("IMPLICIT_CAST_TO_ANY")
class DetailInfoSecFragment : Fragment() {
    private lateinit var info_pref: SharedPreferences
    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit
    private lateinit var scope: CoroutineScope

    private var rest_in = 1
    private var rest_gen_sep = 1
    private var smoking = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_info_sec, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* init */
        pref = requireActivity().getSharedPreferences("owner_info", 0)
        info_pref = requireActivity().getSharedPreferences("cafe_info", 0)
        retrofit = Retrofit.Builder()
            .baseUrl("https://withpresso.gq")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        scope = CoroutineScope(Dispatchers.IO)
        val edit = info_pref.edit()

        detail_info2_layout.setOnClickListener {  }
        detail_info2_linear.setOnClickListener {  }
        detail_info2_anti_corona_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putString("sterilization_info", detail_info2_anti_corona_edit.text.toString()).apply()
                hideKeypad(v)
            }
        }
        detail_info2_discount_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putInt("discount", detail_info2_discount_edit.text.toString().toInt()).apply()
                hideKeypad(v)
            }
        }

        detail_info2_rest_loc_group.setOnCheckedChangeListener { group, checkedId ->
            requireActivity().currentFocus?.let { hideKeypad(it) }

            when(checkedId) {
                R.id.detail_info2_rest_in_radio -> edit.putInt("toilet_info", 1).apply()
                R.id.detail_info2_rest_out_radio -> edit.putInt("toilet_info", 0).apply()
            }
        }
        detail_info2_rest_gen_sep_group.setOnCheckedChangeListener { group, checkedId ->
            requireActivity().currentFocus?.let { hideKeypad(it) }

            when(checkedId) {
                R.id.detail_info2_gen_sep_yes_radio -> edit.putInt("toilet_gender_info", 1).apply()
                R.id.detail_info2_gen_sep_no_radio -> edit.putInt("toilet_gender_info", 0).apply()
            }
        }
        detail_info2_smoking_room_group.setOnCheckedChangeListener { group, checkedId ->
            requireActivity().currentFocus?.let { hideKeypad(it) }

            when(checkedId) {
                R.id.detail_info2_smoking_room_in_radio -> edit.putInt("smoking_room", 1).apply()
                R.id.detail_info2_smoking_room_out_radio -> edit.putInt("smoking_room", 0).apply()
            }
        }

        detail_info2_prev_image.setOnClickListener {
            requireActivity().currentFocus?.let { hideKeypad(it) }
            findNavController().navigate(R.id.action_detailInfoSecFragment_to_detailInfoFragment)
        }

        detail_info2_next_image.setOnClickListener {
            requireActivity().currentFocus?.let { hideKeypad(it) }

            if(!info_pref.contains("toilet_info"))
                toast("화장실 위치 정보를 선택해주세요")
            else if(!info_pref.contains("toilet_gender_info"))
                toast("화장실 성별 분리 여부를 선택해주세요")
            else if(!info_pref.contains("smoking_room"))
                toast("매장 내 흡연실 유무를 선택해주세요")
            else if(!info_pref.contains("sterilization_info"))
                toast("최근 방역 날짜를 입력해주세요")
            else if(!info_pref.contains("discount"))
                toast("재주문 시 할인율을 입력해주세요")
            /* 모든 정보 입력 완료 */
            else {
                /* 카페 기본 정보 */
                val cafe_name = info_pref.getString("cafe_name", null)
                val cafe_hour = info_pref.getString("cafe_hour", null)
                val cafe_addr = info_pref.getString("cafe_addr", null)
                val coord_x = info_pref.getString("coord_x", null)
                val coord_y = info_pref.getString("coord_y", null)
                val cafe_tel = info_pref.getString("cafe_tel", null)
                val cafe_menu = info_pref.getString("cafe_menu", null)

                /* 카페 세부 정보 */
                /* 1.책상 */
                val table_info =
                        "${info_pref.getInt("table_info_seat1", 0)}/" +
                        "${info_pref.getInt("table_info_seat2", 0)}/" +
                        "${info_pref.getInt("table_info_seat4", 0)}/" +
                        "${info_pref.getInt("table_info_multi_seat", 0)}"
                val table_size_info = info_pref.getInt("table_size_info", 0)
                /* 2.의자 */
                val chair_back_info = info_pref.getInt("chair_back_info", 0)
                val chair_cushion_info = info_pref.getString("chair_cushion_info", null)
                /* 3.플러그 */
                val num_plug = info_pref.getInt("num_plug", 0)
                /* 4.음악 */
                val bgm_info = info_pref.getString("bgm_info", null)
                /* 5.화장실 정보 */
                val toilet_info = info_pref.getInt("toilet_info", 0)
                val toilet_gender_info = info_pref.getInt("toilet_gender_info", 0)
                /* 6.방역 날짜 */
                val sterilization_info = info_pref.getString("sterilization_info", null)
                /* 7.흡연실 유무 */
                val smoking_room = info_pref.getInt("smoking_room", 0)
                /* 11.할인율 */
                val discount = info_pref.getInt("discount", 0)
                val cafeInfoRegisterService = retrofit.create(CafeInfoRegisterService::class.java)
                cafeInfoRegisterService.requestCafeInfoRegister(
                    pref.getInt("owner_asin", 0),
                    cafe_name!!, cafe_addr!!, coord_x!!, coord_y!!, cafe_hour!!, cafe_tel!!, cafe_menu!!,
                    table_info, table_size_info!!,
                    chair_back_info!!, chair_cushion_info!!,
                    num_plug!!,
                    bgm_info!!,
                    toilet_info, toilet_gender_info,
                    sterilization_info!!,
                    smoking_room,
                    discount
                ).enqueue(object :Callback<CafeAsin> {
                    override fun onFailure(call: Call<CafeAsin>, t: Throwable) {
                        Log.e("cafe info register", "failed")
                    }

                    override fun onResponse(call: Call<CafeAsin>, response: Response<CafeAsin>) {
                        val result = response.body()
                        result?.let {
                            if (result.cafe_asin == 0)
                                toast("카페 정보 등록을 실패했습니다")
                            else {
                                val edit = pref.edit()
                                edit.putInt("cafe_asin", result.cafe_asin).apply()
                                info_pref.edit().clear().apply()
                                findNavController().navigate(R.id.action_detailInfoSecFragment_to_cafeManageFragment)
                            }
                        }
                    }
                })
            }
        }
    }

    private fun hideKeypad(view: View) {
        view.clearFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}