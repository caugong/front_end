package com.example.withpresso

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.withpresso.service.RatingService
import com.example.withpresso.service.ReviewWriteService
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class Review(
    var cafe_clean: Int,
    var rest_clean: Int,
    var atmo: Int,
    var studied_well: Int
)

@Suppress("IMPLICIT_CAST_TO_ANY")
class ReviewActivity : AppCompatActivity() {
    private lateinit var review: Review
    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit
    private lateinit var BASE_URL: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        /* init */
        review = Review(0, 0, 0, 0)
        pref = getSharedPreferences("user_info", 0)
        BASE_URL = "https://withpresso.gq"
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        /* setOnClickListener */
        review_back_button.setOnClickListener { onBackPressed() }

        review_whole_layout.setOnClickListener {}
        review_cafe_clean_card.setOnClickListener {}
        review_rest_clean_card.setOnClickListener {}
        review_atmo_card.setOnClickListener {}
        review_staff_kind_card.setOnClickListener {}
        review_studied_well_card.setOnClickListener {}

        review_cafe_clean_group.setOnCheckedChangeListener { group, checkedId ->
            when(review_cafe_clean_group.checkedRadioButtonId) {
                R.id.review_cafe_very_dirty_radio -> review.cafe_clean = 1
                R.id.review_cafe_dirty_radio -> review.cafe_clean = 2
                R.id.review_cafe_soso_radio -> review.cafe_clean = 3
                R.id.review_cafe_clean_radio -> review.cafe_clean = 4
                R.id.review_cafe_very_clean_radio -> review.cafe_clean = 5
            }
        }
        review_rest_clean_group.setOnCheckedChangeListener { group, checkedId ->
            when(review_rest_clean_group.checkedRadioButtonId) {
                R.id.review_rest_very_dirty_radio -> review.rest_clean = 1
                R.id.review_rest_dirty_radio -> review.rest_clean = 2
                R.id.review_rest_soso_radio -> review.rest_clean = 3
                R.id.review_rest_clean_radio -> review.rest_clean = 4
                R.id.review_rest_very_clean_radio -> review.rest_clean = 5
            }
        }
        review_atmo_group.setOnCheckedChangeListener { group, checkedId ->
            when(review_atmo_group.checkedRadioButtonId) {
                R.id.review_atmo_very_quiet_radio -> review.atmo = 1
                R.id.review_atmo_quiet_radio -> review.atmo = 2
                R.id.review_atmo_soso_radio -> review.atmo = 3
                R.id.review_atmo_little_noisy_radio -> review.atmo = 4
                R.id.review_atmo_noisy_radio -> review.atmo = 5
            }
        }
        review_studied_well_group.setOnCheckedChangeListener { group, checkedId ->
            when(review_studied_well_group.checkedRadioButtonId) {
                R.id.review_study_very_bad_radio -> review.studied_well = 1
                R.id.review_study_bad_radio -> review.studied_well = 2
                R.id.review_study_soso_radio -> review.studied_well = 3
                R.id.review_study_well_radio -> review.studied_well = 4
                R.id.review_study_very_well_radio -> review.studied_well = 5
            }
        }

        review_comment_edit.setOnClickListener {
            showKeypad(it)
        }
        review_comment_edit.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus)
                hideKeypad(v)
        }

        review_submit_button.setOnClickListener {
            if(review.cafe_clean == 0)
                Toast.makeText(this, "매장은 깨끗했나요?", Toast.LENGTH_SHORT).show()
            else if(review.rest_clean == 0)
                Toast.makeText(this, "화장실은 깨끗했나요?", Toast.LENGTH_SHORT).show()
            else if(review.atmo == 0)
                Toast.makeText(this, "매장 분위기는 괜찮았나요?", Toast.LENGTH_SHORT).show()
            else if(review.studied_well == 0)
                Toast.makeText(this, "공부는 잘 됐나요?", Toast.LENGTH_SHORT).show()
            else if (review_comment_edit.textSize > 400)
                toast("의견은 400자 이내로 작성해주세요")
            else{
                val cafe_asin = intent.getIntExtra("cafe_asin", 0)

                CoroutineScope(Dispatchers.IO).launch {
                    val ratingService = retrofit.create(RatingService::class.java)
                    ratingService.requestReflectRating(
                        review.cafe_clean, review.rest_clean, review.atmo, review.studied_well,
                        cafe_asin, pref.getInt("uniq_num", 0)
                    ).enqueue(object : Callback<Int> {
                        override fun onFailure(call: Call<Int>, t: Throwable) {
                            Log.e("rating service failed", t.message!!)
                        }

                        override fun onResponse(call: Call<Int>, response: Response<Int>) {
                            val result = response.body()
                            result?.let {
                                if (result == 1) {
                                    toast("평점이 잘 전달됐습니다")
                                    Log.d("rating service", "success")
                                }
                            }
                        }
                    })
                }

                if (!review_comment_edit.text.isNullOrBlank()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val reviewWriteService = retrofit.create(ReviewWriteService::class.java)
                        reviewWriteService.requestWriteReview(
                            pref.getInt("uniq_num", 0), cafe_asin, review_comment_edit.text.toString()
                        ).enqueue(object : Callback<Int> {
                            override fun onFailure(call: Call<Int>, t: Throwable) {
                                Log.e("comment service failed", t.message!!)
                            }

                            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                                val result = response.body()
                                result?.let {
                                    if (result == 1) {
                                        toast("소중한 의견이 잘 전달됐습니다")
                                        Log.d("comment service", "success")
                                    }
                                    else {
                                        this@launch.launch(Dispatchers.Main) {
                                            toast("리뷰를 전달하는데 실패했습니다")
                                        }
                                    }
                                }
                            }
                        })
                    }
                }

                onBackPressed()
            }
        }
    }

    private fun showKeypad(view: View) {
        view.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, 0)
    }

    private fun hideKeypad(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

