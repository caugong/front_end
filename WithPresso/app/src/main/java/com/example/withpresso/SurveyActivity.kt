package com.example.withpresso

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.withpresso.R
import com.example.withpresso.service.SurveyService
import kotlinx.android.synthetic.main.activity_my_page.*
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.activity_survey.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SurveyActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit

    private var checked = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        /* init */
        pref = getSharedPreferences("user_info", 0)
        retrofit = Retrofit.Builder()
            .baseUrl("https://withpresso.gq")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        /* setOnClickListener */
        survey_back_button.setOnClickListener {
            onBackPressed()
        }

        survey_table_check.setOnCheckedChangeListener { buttonView, isChecked ->
            manageCheckBox(buttonView,isChecked)
        }
        survey_atmo_check.setOnCheckedChangeListener { buttonView, isChecked ->
            manageCheckBox(buttonView,isChecked)
        }
        survey_light_check.setOnCheckedChangeListener { buttonView, isChecked ->
            manageCheckBox(buttonView,isChecked)
        }
        survey_plug_check.setOnCheckedChangeListener { buttonView, isChecked ->
            manageCheckBox(buttonView,isChecked)
        }
        survey_restroom_check.setOnCheckedChangeListener { buttonView, isChecked ->
            manageCheckBox(buttonView,isChecked)
        }

        survey_submit_button.setOnClickListener {
            if(checked < 3)
                Toast.makeText(this, "답변의 개수가 모자랍니다.", Toast.LENGTH_SHORT).show()
            else {
                var survey = arrayOf(0, 0, 0)
                var idx = 0

                if(survey_table_check.isChecked) survey[idx++] = 1
                if(survey_atmo_check.isChecked) survey[idx++] = 2
                if(survey_light_check.isChecked) survey[idx++] = 3
                if(survey_plug_check.isChecked) survey[idx++] = 4
                if(survey_restroom_check.isChecked) survey[idx] = 5

                /* 설문 결과 서버로 보내기 */
                val surveyService = retrofit.create(SurveyService::class.java)
                surveyService.requestSurvey(
                    pref.getString("email", "")!!, survey[0], survey[1], survey[2]
                ).enqueue(object :Callback<String> {
                    /* 통신 성공 시 실행 */
                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        val surveyResponse = response.body()
                        if (surveyResponse == "1") {
                            /* 조사 결과 저장 */
                            val edit = pref.edit()
                            edit.putInt("survey1", survey[0])
                            edit.putInt("survey2", survey[1])
                            edit.putInt("survey3", survey[2])
                            edit.apply()

                            Toast.makeText(
                                this@SurveyActivity,
                                "조사 결과가 정상적으로 반영되었습니다.",
                                Toast.LENGTH_SHORT
                            ).show()

                            onBackPressed()
                        }
                        else {
                            AlertDialog.Builder(this@SurveyActivity)
                                .setTitle("취향 조사 결과 반영 실패")
                                .setMessage("결과 저장을 실패했습니다.")
                                .show()
                        }
                    }
                    /* 통신 실패 시 실행 */
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        AlertDialog.Builder(this@SurveyActivity)
                            .setTitle("서버 통신 실패")
                            .setMessage("서버와 통신에 실패했습니다.")
                            .show()
                    }
                })
            }

        }
    }

    private fun manageCheckBox(buttonView: View, isChecked: Boolean) {
        buttonView as CheckBox

        if(isChecked) {
            if(checked >= 3) {
                Toast.makeText(this, "3개를 다 골랐습니다.", Toast.LENGTH_SHORT).show()
                buttonView.isChecked = false
            }
            else
                checked++
        }
        else
            checked--
    }
}