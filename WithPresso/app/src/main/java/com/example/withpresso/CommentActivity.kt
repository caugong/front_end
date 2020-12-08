package com.example.withpresso

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.withpresso.adapter.CommentAdapter
import com.example.withpresso.service.Comment
import com.example.withpresso.service.ReadReviewService
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommentActivity : AppCompatActivity() {
    private lateinit var pref: SharedPreferences
    private lateinit var retrofit: Retrofit
    private lateinit var BASE_URL: String
    private lateinit var scope: CoroutineScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        pref = getSharedPreferences("user_info", 0)
        BASE_URL = "https://withpresso.gq"
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        scope = CoroutineScope(Dispatchers.Main)
        comment_recycler.layoutManager = LinearLayoutManager(this)


        comment_back_button.setOnClickListener { onBackPressed() }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        scope.launch(Dispatchers.IO) {
            val cafe_asin = intent.getIntExtra("cafe_asin", 0)
            val readReviewService = retrofit.create(ReadReviewService::class.java)
            val commentList = readReviewService.requestReadReview(cafe_asin)

            this.launch(Dispatchers.Main) {
                toast("cafe_asin: ${cafe_asin}")
                val mean_rating = intent.getFloatExtra("mean_rating", 0f)
                total_ratingBar.rating = mean_rating
                review_mean_point.text = "${getString(R.string.review_point)} ${mean_rating}점"

                total_num_of_review.text = "${getString(R.string.total_num_of_review)} ${commentList.size}개"

                comment_recycler.adapter = CommentAdapter(this@CommentActivity, commentList)
            }
        }
    }
}