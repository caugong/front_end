package com.example.withpresso

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.withpresso.adapter.CafeImagePagerAdapter
import com.example.withpresso.adapter.CafeImagePagerHolder
import com.example.withpresso.adapter.CafeRecyclerViewAdapter
import com.example.withpresso.adapter.ExpandableListAdapter
import com.example.withpresso.service.AuthCodeCheckService
import com.example.withpresso.service.CafeInfo
import com.example.withpresso.service.CafeInfoService
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.cafe_detail_info.view.*
import kotlinx.android.synthetic.main.review_dialog_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.longToast
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class InfoActivity: AppCompatActivity() {
    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit
    private lateinit var BASE_URL: String
    private lateinit var scope: CoroutineScope
    private lateinit var cafeInfo: CafeInfo
    private lateinit var reviewDialog: View

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        /* init */
        pref = getSharedPreferences("user_info", 0)
        retrofit = Retrofit.Builder()
            .baseUrl("https://withpresso.gq")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        BASE_URL = "https://withpresso.gq"
        scope = CoroutineScope(Dispatchers.IO)
        reviewDialog = LayoutInflater.from(this).inflate(R.layout.review_dialog_layout, null)
        cafeInfo = intent.getSerializableExtra("cafe_info") as CafeInfo
        cafeInfo.let {
            val date = it.anco_data.split("/")
            it.cafe_clean = Math.round((it.cafe_clean * 10)) / 10f
            it.rest_clean = Math.round((it.rest_clean * 10)) / 10f
            it.noise = Math.round((it.noise * 10)) / 10f
            it.study_well = Math.round((it.study_well * 10)) / 10f
        }

        /* setOnClickListener */
        cafe_info_swipe.setOnRefreshListener {
            /* 서버에 카페 정보 요청 */
            val cafeInfoService = retrofit.create(CafeInfoService::class.java)
            cafeInfoService.requestCafeInfo(cafeInfo.cafe_asin).enqueue(object :Callback<CafeInfo> {
                /* 통신 실패 시 실행 */
                override fun onFailure(call: Call<CafeInfo>, t: Throwable) {
                    Log.d("request cafeinfo failed", t.message.toString())
                    android.app.AlertDialog.Builder(this@InfoActivity)
                        .setTitle("카페 정보 불러오기 실패")
                        .setMessage("통신 오류")
                        .show()
                }
                /* 통신 성공 시 실행 */
                override fun onResponse(call: Call<CafeInfo>, response: Response<CafeInfo>) {
                    Log.d("retrofit", "executed")
                    val updatedCafeInfo = response.body()
                    updatedCafeInfo?.let{
                        cafeInfo = it
                        cafeInfo.let {
                            val date = it.anco_data.split("/")
                            it.cafe_clean = Math.round((it.cafe_clean * 10)) / 10f
                            it.rest_clean = Math.round((it.rest_clean * 10)) / 10f
                            it.noise = Math.round((it.noise * 10)) / 10f
                            it.study_well = Math.round((it.study_well * 10)) / 10f
                        }
                        onResume()
                    }
                }
            })

            cafe_info_swipe.isRefreshing = false
        }
        info_back_button.setOnClickListener { onBackPressed() }

        /* 세부 정보 자세히 보기 */
        info_more_detail_button.setOnClickListener {
            val moreDetailInfo = LayoutInflater.from(this).inflate(R.layout.cafe_detail_info, null)

            moreDetailInfo.info_detail_all_congestion_text.text = "혼잡도: ${textConverter("level", cafeInfo.level)}"
            moreDetailInfo.info_detail_all_table_text.text =
                "1인석/2인석/4인석/다인석: ${cafeInfo.table_struct}\n" +
                        "넓이(2인석 기준): " + "A4 ${cafeInfo.table_size}장"
            moreDetailInfo.info_detail_all_chair_text.text =
                "쿠션감: ${cafeInfo.chair_cushion}\n" +
                        "등받이: ${textConverter("chair_back", cafeInfo.chair_back)}"
            moreDetailInfo.info_detail_all_outlet_text.text =
                "개수: ${cafeInfo.num_plug}"
            moreDetailInfo.info_detail_all_music_text.text =
                "장르: ${cafeInfo.music_genre}"
            moreDetailInfo.info_detail_all_restroom_text.text =
                "위치: ${textConverter("rest_in", cafeInfo.rest_in)}\n" +
                        "성별 분리: ${textConverter("rest_gen_sep", cafeInfo.rest_gen_sep)}"
            moreDetailInfo.info_detail_all_smoking_room_text.text =
                "유무: ${textConverter("smoking_room", cafeInfo.smoking_room)}"
            moreDetailInfo.info_detail_all_anti_corona_text.text =
                "최근 방역 날짜: ${cafeInfo.anco_data}"
            moreDetailInfo.info_detail_all_cafe_clean_text.text =
                "매장 청결: ${cafeInfo.cafe_clean}점\n" +
                        "화장실 청결: ${cafeInfo.rest_clean}점"
            moreDetailInfo.info_detail_all_noise_text.text =
                "주변 소리(1점 = 시끄러움 ~ 5점 = 조용): ${cafeInfo.noise}점"
            moreDetailInfo.info_detail_all_study_well_text.text =
                "공부 잘 됨 지수: ${cafeInfo.study_well}점"

            AlertDialog.Builder(this)
                .setView(moreDetailInfo)
                .setNegativeButton("뒤로가기") { dialogInterface: DialogInterface, i: Int -> }
                .show()
        }

        info_map_button.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("latLng", cafeInfo.latLng)
            startActivity(intent)
        }

        info_review_button.setOnClickListener {
            if (!(pref.contains("uniq_num") && pref.contains("email") &&
                        pref.contains("password") && pref.contains("nickname"))) {
                longToast("회원 가입 이후에 리뷰를 남길 수 있습니다")
            }
            else {
                reviewDialog.parent?.let { (reviewDialog.parent as ViewGroup).removeView(reviewDialog) }
                pref.getString("phone_num", null)?.let {
                    reviewDialog.phone_number_edit.setText(it)
                }

                AlertDialog.Builder(this)
                    .setView(reviewDialog)
                    .setNegativeButton("취소하기") { _: DialogInterface, _: Int -> }
                    .setPositiveButton("보내기") { _: DialogInterface, _: Int ->
                        /* 입력 받은 전화번호 서버로 보내기 */
                        if (reviewDialog.auth_code.text.toString().isEmpty())
                            toast("인증 코드를 입력해주세요")
                        else if (reviewDialog.phone_number_edit.text.toString().isBlank())
                            toast("휴대폰 번호를 입력해주세요")
                        else {
                            val authCode = reviewDialog.auth_code.text.toString()
                            val authCodeCheckService = retrofit.create(AuthCodeCheckService::class.java)
                            authCodeCheckService.requestAuthCodeCheck(
                                cafeInfo.cafe_asin,
                                reviewDialog.phone_number_edit.text.toString(),
                                authCode
                            ).enqueue(object : Callback<Int> {
                                override fun onFailure(call: Call<Int>, t: Throwable) {
                                    Log.e("send msg request", "failed")
                                    alert("인증 코드 발송을 실패했습니다.")
                                }

                                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                                    val result = response.body()
                                    result?.let {
                                        if (result == 1) {
                                            val edit = pref.edit()
                                            edit.putString("phone_num", reviewDialog.phone_number_edit.text.toString())
                                            edit.apply()

                                            val intent = Intent(this@InfoActivity, ReviewActivity::class.java)
                                            intent.putExtra("cafe_asin", cafeInfo.cafe_asin)
                                            startActivity(intent)
                                        }
                                        else
                                            toast("리뷰 인증에 실패했습니다")
                                    }
                                }
                            })
                        }
                    }.show()
            }
        }

        info_comment_button.setOnClickListener {
            var mean_rating = (cafeInfo.cafe_clean + cafeInfo.rest_clean + cafeInfo.study_well + cafeInfo.noise) / 4
            mean_rating = Math.round(mean_rating * 10) / 10f
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra("cafe_asin", cafeInfo.cafe_asin)
            intent.putExtra("mean_rating", mean_rating)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        /* 카페 이름, 카페 주소 */
        info_cafe_name_text.text = cafeInfo.cafe_name
        info_cafe_addr_text.text = cafeInfo.cafe_addr

        /* 카페 사진 */
        scope.launch {
            val photoList = getPhotoList(cafeInfo.cafe_asin, cafeInfo.num_of_pics)
            scope.launch(Dispatchers.Main) {
                info_cafe_photo_pager.adapter = CafeImagePagerAdapter(this@InfoActivity, photoList)
                info_cafe_photo_pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }
        }

        /* 공부 잘 되는 기준 */
        if(pref.contains("survey1") && pref.contains("survey2") && pref.contains("survey3")) {
            val surveyResult = arrayOf(
                pref.getInt("survey1", 0),
                pref.getInt("survey2", 0),
                pref.getInt("survey3", 0)
            )

            info_recommend_layout.visibility = View.VISIBLE
            setSurveyInfo(surveyResult[0], info_recommend1_image, info_recommend1_text)
            setSurveyInfo(surveyResult[1], info_recommend2_image, info_recommend2_text)
            setSurveyInfo(surveyResult[2], info_recommend3_image, info_recommend3_text)
        }

        /* 기본 정보 */
        info_oper_time_text.text = "운영시간: ${cafeInfo.cafe_hour}"
        info_tel_text.text = "전화번호: ${cafeInfo.cafe_tel}"
        info_menu_text.text = "메뉴: " + "아직 없음."

        /* 세부 정보 */
        info_congestion_text.text =
            "혼잡도: ${textConverter("level", cafeInfo.level)}"
        info_table_text.text =
                        "1인석/2인석/4인석/다인석: ${cafeInfo.table_struct}\n" +
                        "넓이(2인석 기준): " + "A4 ${cafeInfo.table_size}장"
        info_chair_text.text =
                        "쿠션감: ${cafeInfo.chair_cushion}\n" +
                        "등받이: ${textConverter("chair_back", cafeInfo.chair_back)}"
        info_plug_text.text =
                        "개수: ${cafeInfo.num_plug}"
    }

    private fun textConverter(category: String, value: Int): String? {
        return when(category) {
            "chair_back" -> {
                if (value == 1) "있어요."
                else "없어요."
            }
            "rest_in" -> {
                if(value == 1) "매장 안에 있어요."
                else "매장 밖에 있어요."
            }
            "rest_gen_sep" -> {
                if(value == 1) "구분되어 있어요."
                else "구분되어 있지 않아요."
            }
            "smoking_room" -> {
                if(value == 1) "있어요."
                else "없어요."
            }
            "level" -> {
                if (value == 1) "여유 있어요"
                else if (value == 2) "보통이에요"
                else if (value == 3) "혼잡해요"
                else    "영업 시간이 아니에요"
            }
            else -> null
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setSurveyInfo(answer: Int, picto: ImageView, info: TextView) {
        /*
        * 1. 넓은 책상
        * 2. 조용한 분위기
        * 3. 흡연실
        * 4. 많은 콘센트
        * 5. 청결도
        * */
        when(answer) {
            1 -> {
                Glide.with(this)
                    .load(R.drawable.table_color_reverse)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(picto)
                info.text = "A4 ${cafeInfo.table_size}장"
            }
            2-> {
                Glide.with(this)
                    .load(R.drawable.noise_color_reverse)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(picto)
                info.text = "조용한 분위기 ${cafeInfo.noise}점"
            }
            3-> {
                Glide.with(this)
                    .load(R.drawable.smoking_room_color_reverse)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(picto)
                info.text = "흡연실 ${textConverter("smoking_room", cafeInfo.smoking_room)}"
            }
            4-> {
                Glide.with(this)
                    .load(R.drawable.outlet_color_reverse)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(picto)
                info.text = "콘센트 ${cafeInfo.num_plug}개"
            }
            5-> {
                Glide.with(this)
                    .load(R.drawable.clean_color_reverse)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(picto)
                val clean_point = (cafeInfo.cafe_clean + cafeInfo.rest_clean) / 2
                info.text = "청결 점수 ${clean_point}점"
            }
        }
    }

    private fun getPhotoList(cafe_asin: Int, size: Int): ArrayList<String>? {
        if(size == 0)
            return null

        var photoList = MutableList<String>(size) {
                index -> "${BASE_URL}/cafe_pics/${cafe_asin}/${index + 1}.jpg"
        }

        return photoList as ArrayList<String>
    }

    private fun phoneNumFormatCheck(phone: String): Boolean {
        return if(phone.length != 11) false
        else Pattern.matches("^010[0-9]*\$", phone)
    }

}