package com.example.withpressoowner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import com.example.withpressoowner.R
import com.example.withpressoowner.service.LogInService
import com.example.withpressoowner.service.Login
import kotlinx.android.synthetic.main.activity_main.log_in_layout
import kotlinx.android.synthetic.main.fragment_log_in.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LogInFragment : Fragment() {
    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* init */
        pref = requireActivity().getSharedPreferences("owner_info", 0)
        retrofit = Retrofit.Builder()
            .baseUrl("https://withpresso.gq")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        /* 자동 로그인 */
        autoLogIn()

        /* set Listener */
        log_in_layout.setOnClickListener{}
        log_in_id_edit.setOnClickListener { showKeypad(it) }
        log_in_id_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        log_in_pw_edit.setOnClickListener { showKeypad(it) }
        log_in_pw_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }

        log_in_button.setOnClickListener {
            /* 서버로부터 데이터 받아와서 pref에 저장하기 */
            val id = log_in_id_edit.text.toString()
            val pw = log_in_pw_edit.text.toString()
            val logInService = retrofit.create(LogInService::class.java)
            logInService.requestLogin(id, pw).enqueue(object : Callback<Login> {
                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    val logInResult = response.body()

                    logInResult?.let{
                        if (logInResult.owner_asin == 0)
                            toast("로그인 실패")
                        else {
                            val edit = pref.edit()
                            edit.putString("id", log_in_id_edit.text.toString())
                            edit.putString("pw", log_in_pw_edit.text.toString())
                            edit.putInt("cafe_asin", logInResult.cafe_asin)
                            edit.putInt("owner_asin", logInResult.owner_asin)
                            edit.putString("owner_name", logInResult.owner_name)
                            edit.apply()

                            if(pref.getInt("cafe_asin", 0) == 0) {
                                findNavController().navigate(R.id.action_logInFragment_to_basicInfoFragment)
                                edit.remove("cafe_asin").apply()
                            }
                            else {
                                findNavController().navigate(R.id.action_logInFragment_to_cafeManageFragment)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Login>, t: Throwable) {
                    val log = AnkoLogger(this::class.java)
                    log.error("Log in Network error")

                    toast("로그인 실패")
                }
            })
        }

        log_in_sign_up_button.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
        }
    }

    private fun showKeypad(view: View) {
        view.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    private fun hideKeypad(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun autoLogIn() {
        if (pref.contains("id") && pref.contains("pw") &&
            pref.contains("owner_name") && pref.contains("owner_asin") && pref.contains("cafe_asin"))
            findNavController().navigate(R.id.action_logInFragment_to_cafeManageFragment)
        else if (pref.contains("id") && pref.contains("pw") &&
            pref.contains("owner_name") && pref.contains("owner_asin"))
            findNavController().navigate(R.id.action_logInFragment_to_basicInfoFragment)
    }
}