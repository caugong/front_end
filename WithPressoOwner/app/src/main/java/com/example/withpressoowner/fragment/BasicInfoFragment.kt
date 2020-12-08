package com.example.withpressoowner.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.withpressoowner.R
import kotlinx.android.synthetic.main.fragment_basic_info.*
import kotlinx.android.synthetic.main.fragment_sign_up.*
import org.jetbrains.anko.support.v4.toast

class BasicInfoFragment : Fragment() {
    private lateinit var bundle: Bundle
    private lateinit var info_pref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_basic_info, container, false)
    }

    @SuppressLint("UseRequireInsteadOfGet", "CommitPrefEdits")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* init */
        bundle = Bundle()
        arguments?.getString("roadAddr")?.let {
            basic_info_cafe_addr_edit.setText(arguments!!.getString("roadAddr").toString())
        }
        info_pref = requireActivity().getSharedPreferences("cafe_info", 0)
        val edit = info_pref.edit()

        /* event listener */
        basic_info_layout.setOnClickListener {  }
        basic_info_linear.setOnClickListener {  }

        basic_info_cafe_name_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putString("cafe_name", basic_info_cafe_name_edit.text.toString()).apply()
                hideKeypad(v)
            }
        }
        basic_info_hour_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putString("cafe_hour", basic_info_hour_edit.text.toString()).apply()
                hideKeypad(v)
            }
        }
        basic_info_menu_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putString("cafe_menu", basic_info_menu_edit.text.toString()).apply()
                hideKeypad(v)
            }
        }
        basic_info_tel_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                edit.putString("cafe_tel", basic_info_tel_edit.text.toString()).apply()
                hideKeypad(v)
            }
        }
        basic_info_cafe_addr_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                hideKeypad(v)
            }
        }

        basic_info_cafe_addr_button.setOnClickListener {
            findNavController().navigate(R.id.action_basicInfoFragment_to_addressSearchFragment)
        }

        basic_info_prev_image.setOnClickListener {
            requireActivity().currentFocus?.let { hideKeypad(it) }
            requireContext().getSharedPreferences("owner_info", 0).edit().clear().apply()
            findNavController().navigate(R.id.action_basicInfoFragment_to_logInFragment)
        }

        basic_info_next_image.setOnClickListener {
            requireActivity().currentFocus?.let { hideKeypad(it) }

            if (!info_pref.contains("cafe_name"))
                toast("카페 이름을 입력해주세요")
            else if (!(info_pref.contains("cafe_addr") &&
                        info_pref.contains("coord_x") &&
                        info_pref.contains("coord_y")))
                toast("카페 주소를 입력해주세요")
            else if (!info_pref.contains("cafe_hour"))
                toast("카페 운영 시간을 입력해주세요")
            else if (!info_pref.contains("cafe_tel"))
                toast("카페 전화 번호를 입력해주세요")
            else if (!info_pref.contains("cafe_menu"))
                toast("카페 메뉴를 입력해주세요")
            else
                findNavController().navigate(R.id.action_basicInfoFragment_to_detailInfoFragment)
        }
    }

    override fun onResume() {
        super.onResume()

        if (info_pref.contains("cafe_name"))
            basic_info_cafe_name_edit.setText(info_pref.getString("cafe_name", null))
        if (info_pref.contains("cafe_addr"))
            basic_info_cafe_addr_edit.hint = info_pref.getString("cafe_addr", null)
        if (info_pref.contains("cafe_hour"))
            basic_info_hour_edit.setText(info_pref.getString("cafe_hour", null))
        if (info_pref.contains("cafe_tel"))
            basic_info_tel_edit.setText(info_pref.getString("cafe_tel", null))
        if (info_pref.contains("cafe_menu"))
            basic_info_menu_edit.setText(info_pref.getString("cafe_menu", null))
    }

    private fun hideKeypad(view: View) {
        view.clearFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}