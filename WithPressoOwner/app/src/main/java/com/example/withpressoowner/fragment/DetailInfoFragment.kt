package com.example.withpressoowner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.FocusFinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import com.example.withpressoowner.R
import kotlinx.android.synthetic.main.fragment_basic_info.*
import kotlinx.android.synthetic.main.fragment_detail_info.*
import org.jetbrains.anko.support.v4.toast

class DetailInfoFragment : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var info_pref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bundle = Bundle()
        info_pref = requireActivity().getSharedPreferences("cafe_info", 0)
        val edit = info_pref.edit()

        detail_info_layout.setOnClickListener {  }
        detail_info_linear.setOnClickListener {  }
        detail_info_table_size_layout.setOnClickListener {  }
        detail_info_num_outlet_layout.setOnClickListener {  }


        detail_info_seat1_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putInt("table_info_seat1", detail_info_seat1_edit.text.toString().toInt()).apply()
                hideKeypad(v)
            }
        }
        detail_info_seat2_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putInt("table_info_seat2", detail_info_seat2_edit.text.toString().toInt()).apply()
                hideKeypad(v)
            }
        }
        detail_info_seat4_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putInt("table_info_seat4", detail_info_seat4_edit.text.toString().toInt()).apply()
                hideKeypad(v)
            }
        }
        detail_info_multi_seat_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putInt("table_info_multi_seat", detail_info_multi_seat_edit.text.toString().toInt()).apply()
                hideKeypad(v)
            }
        }
        detail_info_table_size_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putInt("table_size_info", detail_info_table_size_edit.text.toString().toInt()).apply()
                hideKeypad(v)
            }
        }
        detail_info_chair_cushion_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putString("chair_cushion_info", detail_info_chair_cushion_edit.text.toString()).apply()
                hideKeypad(v)
            }
        }
        detail_info_num_outlet_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putInt("num_plug", detail_info_num_outlet_edit.text.toString().toInt()).apply()
                hideKeypad(v)
            }
        }
        detail_info_genre_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putString("bgm_info", detail_info_genre_edit.text.toString()).apply()
                hideKeypad(v)
            }
        }

        detail_info_chair_back_group.setOnCheckedChangeListener { group, checkedId ->
            requireActivity().currentFocus?.let { hideKeypad(it) }
            when(detail_info_chair_back_group.checkedRadioButtonId) {
                R.id.detail_info_chair_back_yes_radio -> edit.putInt("chair_back_info", 1).apply()
                R.id.detail_info_chair_back_no_radio -> edit.putInt("chair_back_info", 0).apply()
            }
        }

        detail_info_next_image.setOnClickListener {
            requireActivity().currentFocus?.let { hideKeypad(it) }

            if (!(info_pref.contains("table_info_seat1") &&
                        info_pref.contains("table_info_seat2") &&
                        info_pref.contains("table_info_seat4") &&
                        info_pref.contains("table_info_multi_seat")))
                toast("좌석 정보를 모두 입력해주세요")
            else if(!info_pref.contains("table_size_info"))
                toast("책상 크기를 입력해주세요")
            else if(!info_pref.contains("chair_cushion_info"))
                toast("의자 쿠션감 정보를 입력해주세요")
            else if(!info_pref.contains("chair_back_info"))
                toast("의자 등받이 유무를 선택해주세요")
            else if(!info_pref.contains("num_plug"))
                toast("콘센트 개수를 입력해주세요")
            else if(!info_pref.contains("bgm_info"))
                toast("음악 장르를 입력해주세요")
            else
                findNavController().navigate(R.id.action_detailInfoFragment_to_detailInfoSecFragment)
        }

        detail_info_prev_image.setOnClickListener {
            requireActivity().currentFocus?.let { hideKeypad(it) }
            findNavController().navigate(R.id.action_detailInfoFragment_to_basicInfoFragment)
        }
    }

    override fun onResume() {
        super.onResume()

        /* 좌석 수 */
        if (info_pref.contains("table_info_seat1"))
            detail_info_seat1_edit.setText(info_pref.getInt("table_info_seat1", 0))
        if (info_pref.contains("table_info_seat2"))
            detail_info_seat2_edit.setText(info_pref.getInt("table_info_seat2", 0))
        if (info_pref.contains("table_info_seat4"))
            detail_info_seat4_edit.setText(info_pref.getInt("table_info_seat4", 0))
        if (info_pref.contains("table_info_multi_seat"))
            detail_info_multi_seat_edit.setText(info_pref.getInt("table_info_multi_seat", 0))
        /* 책상 크기 */
        if (info_pref.contains("table_size_info"))
            detail_info_table_size_edit.setText(info_pref.getInt("table_size_info", 0))
        /* 쿠션감 */
        if (info_pref.contains("chair_cushion_info"))
            detail_info_chair_cushion_edit.setText(info_pref.getString("chair_cushion_info", null))
        /* 등받이 */
        if (info_pref.contains("chair_back_info")) {
            if (info_pref.getInt("chair_back_info", 0) == 1) {
                detail_info_chair_back_yes_radio.isChecked = true
                detail_info_chair_back_no_radio.isChecked = false
            }
            else {
                detail_info_chair_back_yes_radio.isChecked = false
                detail_info_chair_back_no_radio.isChecked = true
            }
        }
        /* 콘센트 수 */
        if (info_pref.contains("num_plug"))
            detail_info_num_outlet_edit.setText(info_pref.getInt("num_plug", 0))
        /* 음악 장르 */
        if (info_pref.contains("bgm_info"))
            detail_info_genre_edit.setText(info_pref.getString("bgm_info", null))
    }

    private fun hideKeypad(view: View) {
        view.clearFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}