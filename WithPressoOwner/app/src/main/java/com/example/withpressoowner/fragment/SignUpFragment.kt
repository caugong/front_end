package com.example.withpressoowner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.withpressoowner.R
import com.example.withpressoowner.service.IdDupConfirmService
import com.example.withpressoowner.service.SignUpService
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class SignUpFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit
    private lateinit var scope: CoroutineScope

    private var idDupChecked = false
    private var pwDupChecked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* init */
        pref = requireActivity().getSharedPreferences("owner_info", 0)
        retrofit = Retrofit.Builder()
            .baseUrl("https://withpresso.gq")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        scope = CoroutineScope(Dispatchers.IO)

        /* event listener*/
        sign_up_layout.setOnClickListener{}
        sign_up_linear.setOnClickListener{}
        sign_up_id_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        sign_up_pw_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)

            if(pwDupChecked) {
                pwDupChecked = false

                sign_up_pw_check_edit.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(), R.color.colorWarning)
            }
        }
        sign_up_busi_num_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        sign_up_owner_name_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }
        sign_up_pw_check_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)

            if(sign_up_pw_check_edit.text.toString() == sign_up_pw_edit.text.toString()) {
                sign_up_pw_check_edit.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(), R.color.dark_gray)
            }
            else {
                sign_up_pw_check_edit.backgroundTintList = ContextCompat.getColorStateList(
                    requireContext(), R.color.colorWarning)
            }
        }

        sign_up_id_edit.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(idDupChecked) {
                    idDupChecked = false
                    sign_up_dup_check_button.isClickable = true
                    sign_up_dup_check_button.text = "중복 확인"
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        sign_up_dup_check_button.setOnClickListener {
            val id = sign_up_id_edit.text.toString()

            if(id.isEmpty())
                toast("아이디를 입력해주세요")
            else if(!idFormatCheck(id))
                toast("아이디 형식이 아닙니다")
            else {
                val idDupConfirmService = retrofit.create(IdDupConfirmService::class.java)
                idDupConfirmService.requestIdDupConfirm(id).enqueue(object : Callback<String> {
                    /* 통신이 성공하면 출력 */
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val idDupCheckResult = response.body()

                        idDupCheckResult?.let{
                            if (idDupCheckResult == "0") {
                                idDupChecked = true
                                sign_up_dup_check_button.isClickable = false
                                sign_up_dup_check_button.text = "사용 가능"
                                toast("사용 가능한 아이디입니다")
                            }
                            else
                                toast("이미 사용 중인 아이디입니다")
                        }
                    }
                    /* 통신이 실패하면 출력 */
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.e("id dup check", t.message!!)
                        alert(title = "중복 확인 실패", message = "통신 오류")
                    }
                })
            }
        }

        sign_up_pw_check_edit.addTextChangedListener(object: TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val pw = sign_up_pw_edit.text.toString()

                if(s.toString() == pw) {
                    sign_up_pw_check_edit.backgroundTintList = ContextCompat.getColorStateList(
                        requireContext(), R.color.colorAccent)
                    pwDupChecked = true
                }
                else {
                    sign_up_pw_check_edit.backgroundTintList = ContextCompat.getColorStateList(
                        requireContext(), R.color.colorWarning)
                    pwDupChecked = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        sign_up_next_image.setOnClickListener {
            if(!idDupChecked)
                toast("아이디 중복 확인을 해주세요")
            else if(!pwDupChecked)
                toast("비밀 번호 중복 확인을 해주세요")
            else {
                val owner_id = sign_up_id_edit.text.toString()
                val owner_pw = sign_up_pw_edit.text.toString()
                val owner_name = sign_up_owner_name_edit.text.toString()
                val busi_num = sign_up_busi_num_edit.text.toString()
                val signUpService = retrofit.create(SignUpService::class.java)
                scope.launch {
                    val result = signUpService.requestSignUp(owner_id, owner_pw, owner_name, busi_num)
                    result?.let {
                        if (result.owner_asin != 0) {
                            val edit = pref.edit()
                            edit.putString("id", owner_id)
                            edit.putString("pw", owner_pw)
                            edit.putString("owner_name", owner_name)
                            edit.putInt("owner_asin", result.owner_asin)
                            edit.apply()

                            findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
                        }
                    }
                }
            }
        }
        sign_up_prev_image.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showKeypad(view: View) {
        view.requestFocus()
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    private fun hideKeypad(view: View) {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun idFormatCheck(id: String): Boolean {
        return Pattern.matches("^[a-zA-Z0-9]+$", id)
    }
}